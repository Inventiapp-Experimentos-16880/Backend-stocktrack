package com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources;

public record InvoiceResource(
    String id,
    String number,
    double amount,
    String pdfUrl,
    String status,
    long date
) {}
