package com.inventiapp.stocktrack.subscriptions.domain.model.commands;

import java.time.Instant;

public record CreateSubscriptionCommand(
        Long ownerId,
        String planId,
        String stripeSubscriptionId,
        String stripeCustomerId,
        String status,
        Instant currentPeriodStart,
        Instant currentPeriodEnd,
        boolean cancelAtPeriodEnd
) {
}
