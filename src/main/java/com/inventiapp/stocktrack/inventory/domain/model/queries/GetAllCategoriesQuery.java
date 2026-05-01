package com.inventiapp.stocktrack.inventory.domain.model.queries;

/**
 * GetAllCategoriesQuery
 * @summary
 * GetAllCategoriesQuery is a record class that represents the query to get all categories.
 * @since 1.0
 */
public record GetAllCategoriesQuery(Long ownerId) {
	public GetAllCategoriesQuery {
		if (ownerId == null || ownerId <= 0) {
			throw new IllegalArgumentException("ownerId is required and must be greater than 0");
		}
	}
}
