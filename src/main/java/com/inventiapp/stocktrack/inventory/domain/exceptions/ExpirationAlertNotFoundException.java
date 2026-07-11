package com.inventiapp.stocktrack.inventory.domain.exceptions;

public class ExpirationAlertNotFoundException extends RuntimeException {

    public ExpirationAlertNotFoundException(Long alertId) {
        super("Expiration alert with ID %s was not found".formatted(alertId));
    }
}
