package com.inventiapp.stocktrack.inventory.interfaces.rest.resources;

import java.util.Date;

/**
 * Resource record for a stock movement entry in a batch history timeline.
 *
 * @param id         movement id
 * @param batchId    batch the movement applies to
 * @param productId  product the batch belongs to
 * @param type       movement type (ENTRADA/SALIDA)
 * @param quantity   moved quantity
 * @param occurredAt timestamp when the movement occurred
 */
public record StockMovementResource(
        Long id,
        Long batchId,
        Long productId,
        String type,
        Integer quantity,
        Date occurredAt
) {}
