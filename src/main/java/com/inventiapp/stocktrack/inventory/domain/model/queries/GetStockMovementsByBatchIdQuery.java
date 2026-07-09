package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to fetch the chronological movement history of a batch, scoped by owner.
 *
 * @param batchId batch id
 * @param ownerId owner ID for multi-tenant isolation
 */
public record GetStockMovementsByBatchIdQuery(Long batchId, Long ownerId) {

    public GetStockMovementsByBatchIdQuery {
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("batchId must be a positive number");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
