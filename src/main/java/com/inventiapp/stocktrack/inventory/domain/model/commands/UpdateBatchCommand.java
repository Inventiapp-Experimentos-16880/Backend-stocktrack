package com.inventiapp.stocktrack.inventory.domain.model.commands;

public record UpdateBatchCommand(Long batchId, int newQuantity, Long ownerId) {
    public UpdateBatchCommand {
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("Batch ID must be a positive number");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("New quantity cannot be negative");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
