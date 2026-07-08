package com.inventiapp.stocktrack.inventory.domain.model.valueobject;

/**
 * Type of stock movement recorded against a batch.
 * <p>
 * ENTRADA: quantity added to a batch (initial reception or restock).
 * SALIDA: quantity removed from a batch (sales or any reduction).
 */
public enum MovementType {
    ENTRADA,
    SALIDA
}
