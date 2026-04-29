package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get all batches for an owner.
 */
public record GetAllBatchesQuery(Long ownerId) {
	public GetAllBatchesQuery {
		if (ownerId == null || ownerId <= 0) {
			throw new IllegalArgumentException("ownerId is required and must be greater than 0");
		}
	}
}

