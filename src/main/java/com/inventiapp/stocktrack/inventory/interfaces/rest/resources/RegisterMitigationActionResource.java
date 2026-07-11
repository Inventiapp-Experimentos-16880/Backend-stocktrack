package com.inventiapp.stocktrack.inventory.interfaces.rest.resources;

/**
 * Request payload to register a mitigation action against a near-expiration alert (US17 part 2).
 *
 * @param actionType mitigation action taken; must be a valid MitigationActionType
 *                   (LIQUIDATION or RETURN)
 * @param quantity   units liquidated/returned; optional. When omitted the batch's remaining
 *                   quantity is used. When provided it must be positive and within available stock.
 */
public record RegisterMitigationActionResource(
        String actionType,
        Integer quantity
) {
    public RegisterMitigationActionResource {
        if (actionType == null || actionType.isBlank()) {
            throw new IllegalArgumentException("actionType is required");
        }
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("quantity must be a positive number");
        }
    }
}
