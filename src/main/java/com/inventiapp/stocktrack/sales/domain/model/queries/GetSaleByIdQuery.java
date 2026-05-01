package com.inventiapp.stocktrack.sales.domain.model.queries;

public record GetSaleByIdQuery(Long saleId, Long ownerId) {
    public GetSaleByIdQuery {
        if (saleId == null || saleId <= 0) {
            throw new IllegalArgumentException("Sale ID must be a positive number");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("Owner ID must be a positive number");
        }
    }
}
