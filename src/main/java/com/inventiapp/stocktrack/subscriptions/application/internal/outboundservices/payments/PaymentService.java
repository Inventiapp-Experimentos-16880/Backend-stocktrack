package com.inventiapp.stocktrack.subscriptions.application.internal.outboundservices.payments;

import java.util.Map;

public interface PaymentService {
    String createCustomer(String email, String name, String accountId);
    String createCheckoutSession(String stripeCustomerId, String stripePriceId, String accountId, String planId, String successUrl, String cancelUrl);
    Map<String, String> getSessionMetadata(String sessionId);
    String getOrCreatePrice(String planName, String planDescription, java.math.BigDecimal priceAmount, String currentPriceId);
}
