package com.inventiapp.stocktrack.inventory.domain.model.commands;

import java.util.Date;

/**
 * Command to raise a near-expiration alert for a batch.
 * <p>
 * Issued by the daily expiration scan when a batch expires within the configured threshold.
 * The alert is created in PENDING status.
 *
 * @param batchId        batch the alert applies to
 * @param productId      product the batch belongs to
 * @param expirationDate expiration date of the batch
 * @param triggeredAt    timestamp when the alert was raised
 * @param ownerId        owner ID for multi-tenant isolation
 */
public record RaiseExpirationAlertCommand(
        Long batchId,
        Long productId,
        Date expirationDate,
        Date triggeredAt,
        Long ownerId
) {
    public RaiseExpirationAlertCommand {
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("batchId must be a positive number");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId must be a positive number");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("expirationDate is required");
        }
        if (triggeredAt == null) {
            throw new IllegalArgumentException("triggeredAt is required");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
