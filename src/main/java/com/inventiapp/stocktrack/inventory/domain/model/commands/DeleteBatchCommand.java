package com.inventiapp.stocktrack.inventory.domain.model.commands;

/**
 * Command to delete an existing batch.
 *
 * @param batchId the id of the batch to delete. Cannot be null.
 */
public record DeleteBatchCommand(Long batchId, Long ownerId) {
    public DeleteBatchCommand {
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("batchId must be a positive number");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
