package com.inventiapp.stocktrack.subscriptions.infrastructure.payments.stripe.services;

import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.inventiapp.stocktrack.subscriptions.application.internal.outboundservices.payments.PaymentService;
import org.springframework.stereotype.Service;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripeService implements PaymentService {

    @Override
    public String createCustomer(String email, String name, String accountId) {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(name)
                    .putMetadata("accountId", accountId)
                    .build();
            Customer customer = Customer.create(params);
            return customer.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe customer", e);
        }
    }

    @Override
    public String createCheckoutSession(String stripeCustomerId, String stripePriceId, String accountId, String planId, String successUrl, String cancelUrl) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setCustomer(stripeCustomerId)
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPrice(stripePriceId)
                            .setQuantity(1L)
                            .build())
                    .putMetadata("accountId", accountId)
                    .putMetadata("planId", planId)
                    .setClientReferenceId(accountId)
                    .build();
            Session session = Session.create(params);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe checkout session", e);
        }
    }

    @Override
    public Map<String, String> getSessionMetadata(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return session.getMetadata();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve Stripe session metadata", e);
        }
    }

    @Override
    public String getOrCreatePrice(String planName, String planDescription, java.math.BigDecimal priceAmount, String currentPriceId) {
        if (currentPriceId != null && currentPriceId.startsWith("price_") && !currentPriceId.contains("Placeholder")) {
            try {
                // Verify the price exists on Stripe
                com.stripe.model.Price.retrieve(currentPriceId);
                return currentPriceId;
            } catch (Exception e) {
                // If it doesn't exist, we will create a new one
                log.info("Price {} not found on Stripe, will create a new one", currentPriceId);
            }
        }

        try {
            log.info("Creating Stripe product and price for plan: {}", planName);
            // Create Product
            com.stripe.param.ProductCreateParams productParams = com.stripe.param.ProductCreateParams.builder()
                    .setName("Inventiapp - " + planName)
                    .setDescription(planDescription)
                    .build();
            com.stripe.model.Product product = com.stripe.model.Product.create(productParams);

            // Create Price
            com.stripe.param.PriceCreateParams priceParams = com.stripe.param.PriceCreateParams.builder()
                    .setProduct(product.getId())
                    .setUnitAmount(priceAmount.multiply(new java.math.BigDecimal("100")).longValue())
                    .setCurrency("usd")
                    .setRecurring(com.stripe.param.PriceCreateParams.Recurring.builder()
                            .setInterval(com.stripe.param.PriceCreateParams.Recurring.Interval.MONTH)
                            .build())
                    .build();
            com.stripe.model.Price price = com.stripe.model.Price.create(priceParams);
            log.info("Dynamically created Stripe Product and Price for plan {}: Product ID = {}, Price ID = {}", planName, product.getId(), price.getId());
            return price.getId();
        } catch (Exception e) {
            log.error("Failed to create Stripe product/price dynamically for plan: " + planName, e);
            throw new RuntimeException("Failed to create Stripe product/price dynamically", e);
        }
    }
}
