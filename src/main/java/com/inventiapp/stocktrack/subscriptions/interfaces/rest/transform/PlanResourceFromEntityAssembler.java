package com.inventiapp.stocktrack.subscriptions.interfaces.rest.transform;

import com.inventiapp.stocktrack.subscriptions.domain.model.entities.Plan;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.PlanResource;

public final class PlanResourceFromEntityAssembler {

    private PlanResourceFromEntityAssembler() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static PlanResource toResourceFromEntity(Plan plan) {
        if (plan == null) return null;
        return new PlanResource(
                String.valueOf(plan.getId()),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getBillingInterval(),
                plan.getStripePriceId(),
                plan.getMaxRecipes(),
                plan.getMaxKits(),
                plan.getMaxDevices()
        );
    }
}
