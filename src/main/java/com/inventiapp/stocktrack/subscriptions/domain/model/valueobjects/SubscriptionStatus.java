package com.inventiapp.stocktrack.subscriptions.domain.model.valueobjects;

public enum SubscriptionStatus {
    ACTIVE,
    TRIALING,
    PAST_DUE,
    UNPAID,
    CANCELED,
    INCOMPLETE,
    INCOMPLETE_EXPIRED
}
