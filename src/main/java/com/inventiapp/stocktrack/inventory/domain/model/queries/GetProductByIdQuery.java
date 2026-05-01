package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get a product by id for a specific owner (multi-tenant).
 *
 * @param productId Product id.
 * @param ownerId   Owner id for multi-tenant isolation.
 */
public record GetProductByIdQuery(Long productId, Long ownerId) {
    /**
     * Constructor validation.
     *
     * @param productId Product id. Must be greater than 0 and not null.
     * @param ownerId   Owner id. Must be greater than 0 and not null.
     * @throws IllegalArgumentException if productId or ownerId are invalid.
     */
    public GetProductByIdQuery {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId is required and must be greater than 0");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId is required and must be greater than 0");
        }
    }
}
