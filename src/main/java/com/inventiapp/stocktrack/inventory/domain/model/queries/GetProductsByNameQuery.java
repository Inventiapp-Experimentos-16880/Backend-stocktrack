package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get products by name (partial match) for a specific owner (multi-tenant).
 *
 * @param ownerId Owner id for multi-tenant isolation.
 * @param name    Product name or query to search for.
 */
public record GetProductsByNameQuery(Long ownerId, String name) {
    /**
     * Constructor validation.
     *
     * @param ownerId Owner id. Must be greater than 0 and not null.
     * @param name    Product name search query. Must not be null or blank.
     * @throws IllegalArgumentException if ownerId is invalid or name is null/blank.
     */
    public GetProductsByNameQuery {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId is required and must be greater than 0");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required and cannot be blank");
        }
    }
}
