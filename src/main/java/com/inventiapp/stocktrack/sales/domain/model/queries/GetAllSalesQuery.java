package com.inventiapp.stocktrack.sales.domain.model.queries;

public record GetAllSalesQuery(Long ownerId) {
    public GetAllSalesQuery {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("Owner ID must be a positive number");
        }
    }
}
