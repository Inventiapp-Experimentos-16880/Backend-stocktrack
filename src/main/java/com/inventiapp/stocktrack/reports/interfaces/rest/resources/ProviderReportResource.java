package com.inventiapp.stocktrack.reports.interfaces.rest.resources;

public record ProviderReportResource(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phoneNumber,
        String ruc,
        Boolean isDeleted
) {}