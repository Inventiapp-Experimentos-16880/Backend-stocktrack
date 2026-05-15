package com.inventiapp.stocktrack.reports.interfaces.rest;

import com.inventiapp.stocktrack.reports.application.ProviderReportService;
import com.inventiapp.stocktrack.reports.interfaces.rest.resources.ProviderReportResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for provider report endpoints.
 * Provides provider data including deleted ones for reporting purposes.
 */
@RestController
@RequestMapping(value = "/api/v1/reports/providers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reports", description = "Report endpoints for aggregated data")
@SecurityRequirement(name = "bearerAuth")
public class ProviderReportController {

    private final ProviderReportService providerReportService;

    public ProviderReportController(ProviderReportService providerReportService) {
        this.providerReportService = providerReportService;
    }

    @GetMapping
    @Operation(
            summary = "Get all providers for report",
            description = "Retrieves all providers including deleted ones for reporting purposes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Providers retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid token required")
    })
    public ResponseEntity<List<ProviderReportResource>> getProviderReport() {
        return ResponseEntity.ok(providerReportService.getProviderReport());
    }
}