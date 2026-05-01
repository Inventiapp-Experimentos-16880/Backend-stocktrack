package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * GetAllKitsQuery
 * 
 * @summary
 * GetAllKitsQuery is a record class that represents the query to get all kits.
 * @since 1.0
 */
public record GetAllKitsQuery(Long ownerId) {
	public GetAllKitsQuery {
		if (ownerId == null || ownerId <= 0) {
			throw new IllegalArgumentException("ownerId is required and must be greater than 0");
		}
	}
}

