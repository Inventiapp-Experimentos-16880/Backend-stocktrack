package com.inventiapp.stocktrack.inventory.domain.model.valueobject;

/**
 * Type of mitigation action registered against a near-expiration alert (US17 part 2).
 * <p>
 * LIQUIDATION: the batch is sold off / discarded to cut losses.
 * RETURN: the batch is returned to the provider.
 * <p>
 * Both actions imply a stock decrease (SALIDA) for the associated batch.
 */
public enum MitigationActionType {
    LIQUIDATION,
    RETURN
}
