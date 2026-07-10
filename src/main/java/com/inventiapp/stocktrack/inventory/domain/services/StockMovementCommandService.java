package com.inventiapp.stocktrack.inventory.domain.services;

import com.inventiapp.stocktrack.inventory.domain.model.commands.RecordStockMovementCommand;

/**
 * Command service for the StockMovement aggregate.
 * Records inventory movements (ENTRADA/SALIDA) as an additive audit trail.
 */
public interface StockMovementCommandService {

    /**
     * Handle recording a stock movement.
     *
     * @param command the record movement command
     * @return generated stock movement id
     */
    Long handle(RecordStockMovementCommand command);
}
