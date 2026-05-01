package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get provider by id for a specific owner (multi-tenant).
 * @param providerId Provider id.
 * @param ownerId Owner id for multi-tenant isolation.
 */
public record GetProviderByIdQuery(Long providerId, Long ownerId) {
    /**
     * Constructor.
     * @param providerId Provider id. Must be greater than 0 and not null.
     * @param ownerId Owner id. Must be greater than 0 and not null.
     * @throws IllegalArgumentException If the provider ID or ownerId is invalid.
     */
    public GetProviderByIdQuery {
        if (providerId == null || providerId <= 0)
            throw new IllegalArgumentException("providerId is required and must be greater than 0.");
        if (ownerId == null || ownerId <= 0)
            throw new IllegalArgumentException("ownerId is required and must be greater than 0.");
    }
}
