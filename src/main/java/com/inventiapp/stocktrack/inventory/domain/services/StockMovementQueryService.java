package com.inventiapp.stocktrack.inventory.domain.services;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.StockMovement;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetStockMovementsByBatchIdQuery;

import java.util.List;

/**
 * Query service for the StockMovement aggregate.
 * Provides read access to the chronological movement history of a batch.
 */
public interface StockMovementQueryService {

    /**
     * Handle query to get the movements of a batch in chronological order.
     *
     * @param query get stock movements by batch id query
     * @return list of movements ordered by occurredAt ascending
     */
    List<StockMovement> handle(GetStockMovementsByBatchIdQuery query);
}
