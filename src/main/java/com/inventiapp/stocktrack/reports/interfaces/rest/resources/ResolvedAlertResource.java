package com.inventiapp.stocktrack.reports.interfaces.rest.resources;

/**
 * Resource representing a resolved alert with its savings details.
 * @param product Product name
 * @param quantity Quantity resolved
 * @param amount Amount saved in currency
 * @param date Date of resolution (yyyy-MM-dd)
 */
public record ResolvedAlertResource(
        String product,
        int quantity,
        double amount,
        String date
) {}
