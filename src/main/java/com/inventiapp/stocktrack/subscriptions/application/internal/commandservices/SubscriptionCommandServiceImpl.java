package com.inventiapp.stocktrack.subscriptions.application.internal.commandservices;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.inventiapp.stocktrack.subscriptions.application.internal.outboundservices.payments.PaymentService;
import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Account;
import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Subscription;
import com.inventiapp.stocktrack.subscriptions.domain.model.commands.CreateSubscriptionCommand;
import com.inventiapp.stocktrack.subscriptions.domain.model.entities.Plan;
import com.inventiapp.stocktrack.subscriptions.domain.model.valueobjects.AccountStatus;
import com.inventiapp.stocktrack.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.AccountRepository;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.PlanRepository;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.SubscriptionRepository;
import com.inventiapp.stocktrack.subscriptions.domain.services.SubscriptionCommandService;
import com.inventiapp.stocktrack.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.inventiapp.stocktrack.iam.domain.model.aggregates.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final AccountRepository accountRepository;
    private final PlanRepository planRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository,
                                           AccountRepository accountRepository,
                                           PlanRepository planRepository,
                                           PaymentService paymentService,
                                           UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.accountRepository = accountRepository;
        this.planRepository = planRepository;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Subscription> handle(CreateSubscriptionCommand command) {
        Plan plan = planRepository.findById(Long.parseLong(command.planId()))
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with ID: " + command.planId()));

        Account account = accountRepository.findByOwnerId(command.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for ownerId: " + command.ownerId()));

        var existingSub = subscriptionRepository.findByAccountTableId(account.getId());
        Subscription subscription;

        if (existingSub.isPresent()) {
            subscription = existingSub.get();
            subscription.setPlanId(String.valueOf(plan.getId()));
            subscription.setStripeSubscriptionId(command.stripeSubscriptionId());
            subscription.setStatus(SubscriptionStatus.valueOf(command.status().toUpperCase()));
            subscription.setCurrentPeriodStart(command.currentPeriodStart());
            subscription.setCurrentPeriodEnd(command.currentPeriodEnd());
            subscription.setCancelAtPeriodEnd(command.cancelAtPeriodEnd());
        } else {
            subscription = Subscription.builder()
                    .accountTableId(account.getId())
                    .planId(String.valueOf(plan.getId()))
                    .stripeSubscriptionId(command.stripeSubscriptionId())
                    .status(SubscriptionStatus.valueOf(command.status().toUpperCase()))
                    .currentPeriodStart(command.currentPeriodStart())
                    .currentPeriodEnd(command.currentPeriodEnd())
                    .cancelAtPeriodEnd(command.cancelAtPeriodEnd())
                    .build();
            subscription.assignOwnerId(command.ownerId());
        }

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Subscription persisted locally: id={}, ownerId={}, planId={}", saved.getId(), command.ownerId(), plan.getId());
        return Optional.of(saved);
    }

    @Override
    public String createCheckoutSession(Long ownerId, String planId, boolean isOnboarding) {
        Account account = accountRepository.findByOwnerId(ownerId)
                .orElseGet(() -> {
                    log.info("Subscription account not found for ownerId: {}. Auto-creating...", ownerId);
                    String email = userRepository.findById(ownerId).map(User::getEmail).orElse("owner_" + ownerId + "@example.com");
                    Account newAccount = Account.builder()
                            .email(email)
                            .stripeCustomerId(null)
                            .currentPlanId(null)
                            .status(AccountStatus.INACTIVE)
                            .build();
                    newAccount.assignOwnerId(ownerId);
                    return accountRepository.save(newAccount);
                });

        Plan plan = planRepository.findById(Long.parseLong(planId))
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with ID: " + planId));

        if (account.getStripeCustomerId() == null || account.getStripeCustomerId().isBlank()) {
            log.info("Creating Stripe Customer for account owner: {}", ownerId);
            String stripeCustomerId = paymentService.createCustomer(account.getEmail(), String.valueOf(ownerId), String.valueOf(ownerId));
            account.setStripeCustomerId(stripeCustomerId);
            accountRepository.save(account);
        }

        // Dynamically verify or create Stripe Product and Price
        String realStripePriceId = paymentService.getOrCreatePrice(plan.getName(), plan.getDescription(), plan.getPrice(), plan.getStripePriceId());
        if (!realStripePriceId.equals(plan.getStripePriceId())) {
            log.info("Updating Plan stripePriceId to: {}", realStripePriceId);
            plan.setStripePriceId(realStripePriceId);
            planRepository.save(plan);
        }

        String finalSuccessUrl = successUrl;
        String finalCancelUrl = cancelUrl;
        if (isOnboarding) {
            finalSuccessUrl += (finalSuccessUrl.contains("?") ? "&" : "?") + "onboarding=true";
            finalCancelUrl += (finalCancelUrl.contains("?") ? "&" : "?") + "onboarding=true";
        }

        log.info("Generating Stripe Checkout Session for ownerId={}, plan={}, stripePriceId={}, successUrl={}", ownerId, planId, realStripePriceId, finalSuccessUrl);
        return paymentService.createCheckoutSession(
                account.getStripeCustomerId(),
                realStripePriceId,
                String.valueOf(ownerId),
                planId,
                finalSuccessUrl,
                finalCancelUrl
        );
    }

    @Override
    public void handleStripeWebhook(String payload, String sigHeader) {
        Event event;
        try {
            if (webhookSecret == null || webhookSecret.isBlank() || webhookSecret.equals("whsec_WebhookSecretPlaceholderSecretKey")) {
                log.info("Bypassing Stripe signature verification (dev environment fallback)");
                event = Event.GSON.fromJson(payload, Event.class);
            } else {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            }
        } catch (Exception e) {
            log.error("Signature verification failed: {}", e.getMessage());
            throw new RuntimeException("Webhook Signature Verification Failed", e);
        }

        log.info("Processing Stripe Webhook Event: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "customer.subscription.updated":
                handleSubscriptionUpdated(event);
                break;
            case "customer.subscription.deleted":
                handleSubscriptionDeleted(event);
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = deserializeEventObject(event, Session.class);
        if (session == null) {
            log.warn("Checkout Session object is null");
            return;
        }

        String resolvedAccountIdStr = session.getClientReferenceId();
        if (resolvedAccountIdStr == null) {
            resolvedAccountIdStr = session.getMetadata() != null ? session.getMetadata().get("accountId") : null;
        }
        final String accountIdStr = resolvedAccountIdStr;
        String planId = session.getMetadata() != null ? session.getMetadata().get("planId") : null;
        String stripeSubscriptionId = session.getSubscription();
        String stripeCustomerId = session.getCustomer();

        if (accountIdStr == null || planId == null || stripeSubscriptionId == null) {
            log.warn("Missing checkout metadata: accountId={}, planId={}, stripeSubscriptionId={}", accountIdStr, planId, stripeSubscriptionId);
            return;
        }

        log.info("Checkout completed. AccountId: {}, PlanId: {}, StripeSubscriptionId: {}", accountIdStr, planId, stripeSubscriptionId);

        try {
            com.stripe.model.Subscription stripeSub = com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
            Long ownerId = Long.parseLong(accountIdStr);

            CreateSubscriptionCommand cmd = new CreateSubscriptionCommand(
                    ownerId,
                    planId,
                    stripeSubscriptionId,
                    stripeCustomerId,
                    stripeSub.getStatus(),
                    Instant.ofEpochSecond(stripeSub.getCurrentPeriodStart()),
                    Instant.ofEpochSecond(stripeSub.getCurrentPeriodEnd()),
                    stripeSub.getCancelAtPeriodEnd()
            );

            handle(cmd);

            // Update Account info
            Account account = accountRepository.findByOwnerId(ownerId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + ownerId));
            account.setCurrentPlanId(planId);
            account.setStripeCustomerId(stripeCustomerId);
            account.setStatus(mapStripeStatusToAccountStatus(stripeSub.getStatus()));
            accountRepository.save(account);

        } catch (Exception e) {
            log.error("Error processing checkout session completion", e);
            throw new RuntimeException("Error processing checkout session: " + e.getMessage(), e);
        }
    }

    private void handleSubscriptionUpdated(Event event) {
        com.stripe.model.Subscription stripeSub = deserializeEventObject(event, com.stripe.model.Subscription.class);
        if (stripeSub == null) {
            log.warn("Subscription object is null");
            return;
        }

        log.info("Subscription updated. Stripe ID: {}, Status: {}", stripeSub.getId(), stripeSub.getStatus());

        Optional<Subscription> localSubOpt = subscriptionRepository.findByStripeSubscriptionId(stripeSub.getId());
        if (localSubOpt.isPresent()) {
            Subscription localSub = localSubOpt.get();
            localSub.setStatus(SubscriptionStatus.valueOf(stripeSub.getStatus().toUpperCase()));
            localSub.setCurrentPeriodStart(Instant.ofEpochSecond(stripeSub.getCurrentPeriodStart()));
            localSub.setCurrentPeriodEnd(Instant.ofEpochSecond(stripeSub.getCurrentPeriodEnd()));
            localSub.setCancelAtPeriodEnd(stripeSub.getCancelAtPeriodEnd());
            subscriptionRepository.save(localSub);

            // Update Account status
            Optional<Account> accountOpt = accountRepository.findById(localSub.getAccountTableId());
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                account.setStatus(mapStripeStatusToAccountStatus(stripeSub.getStatus()));
                accountRepository.save(account);
            }
        }
    }

    private void handleSubscriptionDeleted(Event event) {
        com.stripe.model.Subscription stripeSub = deserializeEventObject(event, com.stripe.model.Subscription.class);
        if (stripeSub == null) {
            log.warn("Subscription object is null");
            return;
        }

        log.info("Subscription deleted / canceled. Stripe ID: {}", stripeSub.getId());

        Optional<Subscription> localSubOpt = subscriptionRepository.findByStripeSubscriptionId(stripeSub.getId());
        if (localSubOpt.isPresent()) {
            Subscription localSub = localSubOpt.get();
            localSub.setStatus(SubscriptionStatus.CANCELED);
            subscriptionRepository.save(localSub);

            Optional<Account> accountOpt = accountRepository.findById(localSub.getAccountTableId());
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                account.setStatus(AccountStatus.CANCELED);
                account.setCurrentPlanId(null);
                accountRepository.save(account);
            }
        }
    }

    private AccountStatus mapStripeStatusToAccountStatus(String stripeStatus) {
        switch (stripeStatus) {
            case "active":
                return AccountStatus.ACTIVE;
            case "trialing":
                return AccountStatus.TRIALING;
            case "canceled":
            case "incomplete_expired":
                return AccountStatus.CANCELED;
            default:
                return AccountStatus.INACTIVE;
        }
    }

    private <T> T deserializeEventObject(Event event, Class<T> clazz) {
        var deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isPresent()) {
            return clazz.cast(deserializer.getObject().get());
        }
        try {
            return clazz.cast(deserializer.deserializeUnsafe());
        } catch (Exception e) {
            log.error("Failed to deserialize event object for type: " + event.getType(), e);
            return null;
        }
    }
}
