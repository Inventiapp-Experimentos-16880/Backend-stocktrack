package com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources;

import java.math.BigDecimal;

public record PlanResource(
        String id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        String billingInterval,
        String stripePriceId,
        int maxRecipes,
        int maxKits,
        int maxDevices
) {
}
