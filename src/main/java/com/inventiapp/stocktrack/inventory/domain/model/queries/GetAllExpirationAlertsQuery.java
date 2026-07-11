package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get all expiration alerts for an owner.
 *
 * @param ownerId owner ID for multi-tenant isolation
 */
public record GetAllExpirationAlertsQuery(Long ownerId) {

    public GetAllExpirationAlertsQuery {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
