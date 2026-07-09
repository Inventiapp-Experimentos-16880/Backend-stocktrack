package com.inventiapp.stocktrack.inventory.interfaces.rest.transform;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.StockMovement;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.StockMovementResource;

/**
 * Assembler to convert a StockMovement aggregate to a StockMovementResource.
 */
public class StockMovementResourceFromEntityAssembler {
    /**
     * Converts a StockMovement entity into a StockMovementResource.
     *
     * @param movement the StockMovement aggregate
     * @return StockMovementResource for API responses
     */
    public static StockMovementResource toResource(StockMovement movement) {
        if (movement == null) {
            throw new IllegalArgumentException("StockMovement cannot be null");
        }

        return new StockMovementResource(
                movement.getId(),
                movement.getBatchId(),
                movement.getProductId(),
                movement.getType().name(),
                movement.getQuantity(),
                movement.getOccurredAt()
        );
    }
}
