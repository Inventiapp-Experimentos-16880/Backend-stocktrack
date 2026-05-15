package com.inventiapp.stocktrack.inventory.domain.exceptions;

/**
 * Exception thrown when attempting to delete a product that has existing stock.
 */
public class ProductHasStockException extends RuntimeException {
    public ProductHasStockException(String message) {
        super(message);
    }
}
