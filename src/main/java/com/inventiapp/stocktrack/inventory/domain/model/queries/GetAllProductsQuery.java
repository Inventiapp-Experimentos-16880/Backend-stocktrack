package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * Query to get all products for a specific owner (multi-tenant).
 *
 * @param ownerId Owner id for multi-tenant isolation.
 */
public record GetAllProductsQuery(Long ownerId) {
	public GetAllProductsQuery {
		if (ownerId == null || ownerId <= 0) {
			throw new IllegalArgumentException("ownerId is required and must be greater than 0");
		}
	}
}