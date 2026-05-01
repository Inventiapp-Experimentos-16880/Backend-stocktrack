package com.inventiapp.stocktrack.sales.domain.model.commands;


import java.util.List;

public record CreateSaleCommand(long staffUserId, double totalAmount, List<SaleDetailItem> details, Long ownerId) {
    public CreateSaleCommand {

        if (staffUserId <= 0) {
            throw new IllegalArgumentException("Staff user ID must be positive");
        }
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }

        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("Sale details cannot be null or empty");
        }

        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId cannot be null or non-positive");
        }
    }
}

