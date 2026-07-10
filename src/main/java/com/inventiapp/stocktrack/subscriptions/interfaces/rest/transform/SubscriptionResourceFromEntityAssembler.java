package com.inventiapp.stocktrack.subscriptions.interfaces.rest.transform;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Subscription;
import com.inventiapp.stocktrack.subscriptions.domain.model.entities.Plan;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.SubscriptionResource;

public final class SubscriptionResourceFromEntityAssembler {

    private SubscriptionResourceFromEntityAssembler() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static SubscriptionResource toResourceFromEntity(Subscription subscription, Plan plan) {
        if (subscription == null) return null;
        return new SubscriptionResource(
                String.valueOf(subscription.getId()),
                String.valueOf(subscription.getAccountTableId()),
                subscription.getPlanId(),
                plan != null ? plan.getName() : "None",
                plan != null ? plan.getPrice() : java.math.BigDecimal.ZERO,
                subscription.getStripeSubscriptionId(),
                subscription.getStatus().name(),
                subscription.getCurrentPeriodEnd(),
                subscription.isCancelAtPeriodEnd(),
                plan != null ? plan.getMaxRecipes() : 0,
                plan != null ? plan.getMaxKits() : 0,
                plan != null ? plan.getMaxDevices() : 0
        );
    }
}
