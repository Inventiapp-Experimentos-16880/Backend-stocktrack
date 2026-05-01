package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get a kit by id.
 * 
 * @param kitId Kit id.
 * @since 1.0
 */
public record GetKitByIdQuery(Long kitId, Long ownerId) {
    /**
     * Constructor validation.
     * 
     * @param kitId Kit id. Must be greater than 0 and not null.
     * @param ownerId Owner id. Must be greater than 0 and not null.
     * @throws IllegalArgumentException if kitId or ownerId are invalid.
     */
    public GetKitByIdQuery {
        if (kitId == null || kitId <= 0) {
            throw new IllegalArgumentException("kitId is required and must be greater than 0");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId is required and must be greater than 0");
        }
    }
}

