package com.inventiapp.stocktrack.subscriptions.domain.services;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Subscription;
import com.inventiapp.stocktrack.subscriptions.domain.model.commands.CreateSubscriptionCommand;
import java.util.Optional;

public interface SubscriptionCommandService {
    Optional<Subscription> handle(CreateSubscriptionCommand command);
    String createCheckoutSession(Long ownerId, String planId, boolean isOnboarding);
    void handleStripeWebhook(String payload, String sigHeader);
}
