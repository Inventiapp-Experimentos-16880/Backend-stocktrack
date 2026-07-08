package com.inventiapp.stocktrack.inventory.domain.model.commands;

import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MovementType;

import java.util.Date;

/**
 * Command to record a stock movement (ENTRADA/SALIDA) against a batch.
 * <p>
 * The movement is an additive audit record; it does not alter the batch stock itself,
 * which is handled by the existing batch command flow.
 *
 * @param productId  product the batch belongs to
 * @param batchId    batch the movement applies to
 * @param type       movement type (ENTRADA/SALIDA)
 * @param quantity   moved quantity (always positive)
 * @param occurredAt timestamp when the movement occurred
 * @param ownerId    owner ID for multi-tenant isolation
 */
public record RecordStockMovementCommand(
        Long productId,
        Long batchId,
        MovementType type,
        Integer quantity,
        Date occurredAt,
        Long ownerId
) {
    public RecordStockMovementCommand {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId must be a positive number");
        }
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("batchId must be a positive number");
        }
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be a positive number");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt is required");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
