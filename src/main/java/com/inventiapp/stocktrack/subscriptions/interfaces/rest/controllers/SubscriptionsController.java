package com.inventiapp.stocktrack.subscriptions.interfaces.rest.controllers;

import com.inventiapp.stocktrack.subscriptions.domain.model.queries.GetSubscriptionByAccountIdQuery;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.PlanRepository;
import com.inventiapp.stocktrack.subscriptions.domain.services.SubscriptionCommandService;
import com.inventiapp.stocktrack.subscriptions.domain.services.SubscriptionQueryService;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.CheckoutSessionResponseResource;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.CreateCheckoutSessionResource;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.SubscriptionResource;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.InvoiceResource;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = "application/json")
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
public class SubscriptionsController {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final PlanRepository planRepository;

    public SubscriptionsController(SubscriptionCommandService subscriptionCommandService,
                                   SubscriptionQueryService subscriptionQueryService,
                                   PlanRepository planRepository) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
        this.planRepository = planRepository;
    }

    @Operation(summary = "Get active subscription details")
    @GetMapping
    public ResponseEntity<SubscriptionResource> getSubscriptionStatus(@RequestParam String accountId) {
        Long ownerId = Long.parseLong(accountId);
        var query = new GetSubscriptionByAccountIdQuery(ownerId);
        var subOpt = subscriptionQueryService.handle(query);
        if (subOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var sub = subOpt.get();
        var plan = planRepository.findById(Long.parseLong(sub.getPlanId())).orElse(null);
        return ResponseEntity.ok(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(sub, plan));
    }

    @Operation(summary = "Create checkout session for Stripe Checkout redirect")
    @PostMapping("/checkout-session")
    public ResponseEntity<CheckoutSessionResponseResource> createCheckoutSession(@RequestBody CreateCheckoutSessionResource resource) {
        Long ownerId = Long.parseLong(resource.accountId());
        boolean isOnboarding = resource.onboarding() != null && resource.onboarding();
        String sessionUrl = subscriptionCommandService.createCheckoutSession(ownerId, resource.planId(), isOnboarding);
        return ResponseEntity.ok(new CheckoutSessionResponseResource(sessionUrl));
    }

    @Operation(summary = "Get Stripe invoices / billing history for account")
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResource>> getBillingHistory(@RequestParam String accountId) {
        Long ownerId = Long.parseLong(accountId);
        List<InvoiceResource> invoices = subscriptionQueryService.getInvoicesByAccountId(ownerId);
        return ResponseEntity.ok(invoices);
    }
}
