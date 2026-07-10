package com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionResource(
        String id,
        String accountId,
        String planId,
        String planName,
        BigDecimal planPrice,
        String stripeSubscriptionId,
        String status,
        Instant currentPeriodEnd,
        boolean cancelAtPeriodEnd,
        int maxRecipes,
        int maxKits,
        int maxDevices
) {
}
