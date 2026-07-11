package com.inventiapp.stocktrack.inventory.domain.exceptions;

/**
 * Raised when a mitigation action is registered against an alert that is no longer PENDING.
 * Signals an invalid state transition (maps to HTTP 409 at the REST boundary).
 */
public class AlertAlreadyResolvedException extends RuntimeException {

    public AlertAlreadyResolvedException(Long alertId) {
        super("Expiration alert with ID %s has already been resolved".formatted(alertId));
    }
}
