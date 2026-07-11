package com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources;

public record CreateCheckoutSessionResource(
        String accountId,
        String planId,
        Boolean onboarding
) {
}
