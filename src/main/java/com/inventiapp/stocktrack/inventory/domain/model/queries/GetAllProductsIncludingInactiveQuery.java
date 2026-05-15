package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get all products for an owner, including inactive ones.
 *
 * @param ownerId the owner ID for multi-tenant isolation. Cannot be null or non-positive.
 */
public record GetAllProductsIncludingInactiveQuery(Long ownerId) {
    public GetAllProductsIncludingInactiveQuery {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
