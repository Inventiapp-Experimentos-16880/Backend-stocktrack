package com.inventiapp.stocktrack.inventory.domain.model.commands;

import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MitigationActionType;

/**
 * Command to register a mitigation action (liquidation/return) against a near-expiration alert
 * (US17 part 2). Resolving the alert also drives the stock decrease of the associated batch.
 *
 * @param alertId    id of the alert to resolve
 * @param actionType the mitigation action taken
 * @param quantity   units liquidated/returned; null defaults to the batch's remaining quantity.
 *                   When provided it must be positive and not exceed the batch's available stock.
 * @param ownerId    owner ID for multi-tenant isolation
 */
public record RegisterMitigationActionCommand(
        Long alertId,
        MitigationActionType actionType,
        Integer quantity,
        Long ownerId
) {
    public RegisterMitigationActionCommand {
        if (alertId == null || alertId <= 0) {
            throw new IllegalArgumentException("alertId must be a positive number");
        }
        if (actionType == null) {
            throw new IllegalArgumentException("actionType is required");
        }
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("quantity must be a positive number");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }
    }
}
