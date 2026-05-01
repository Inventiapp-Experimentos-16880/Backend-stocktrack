package com.inventiapp.stocktrack.inventory.domain.model.commands;

import java.util.Date;

/**
 * Command to create a batch.
 *
 * @param productId the product id. Cannot be null or less than or equal to 0.
 * @param quantity the stock quantity. Cannot be null or negative.
 * @param expirationDate the expiration date. Cannot be null.
 * @param receptionDate the reception date. Cannot be null.
 * @param ownerId the owner ID for multi-tenant isolation. Cannot be null or non-positive.
 * @since 1.0
 */
public record CreateBatchCommand(
        Long productId,
        Integer quantity,
        Date expirationDate,
        Date receptionDate,
        Long ownerId
) {
    /**
     * Constructor validation.
     *
     * @throws IllegalArgumentException if any required field is missing or invalid.
     */
    public CreateBatchCommand {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId cannot be null or less than or equal to 0");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("quantity cannot be null or negative");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("expirationDate cannot be null");
        }
        if (receptionDate == null) {
            throw new IllegalArgumentException("receptionDate cannot be null");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId cannot be null or non-positive");
        }
    }
}
