package com.inventiapp.stocktrack.inventory.domain.model.queries;

import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;

/**
 * Query to get expiration alerts for an owner filtered by status.
 *
 * @param ownerId owner ID for multi-tenant isolation
 * @param status  alert status to filter by (PENDING/RESOLVED)
 */
public record GetExpirationAlertsByStatusQuery(Long ownerId, AlertStatus status) {

    public GetExpirationAlertsByStatusQuery {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }
    }
}
