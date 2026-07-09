package com.inventiapp.stocktrack.inventory.interfaces.rest.controllers;

import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllExpirationAlertsQuery;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.ExpirationAlertResource;
import com.inventiapp.stocktrack.inventory.interfaces.rest.transform.ExpirationAlertResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller exposing the expiration alerts of the authenticated owner.
 */
@RestController
@RequestMapping(value = "/api/v1/expiration-alerts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Expiration Alerts", description = "Endpoints for near-expiration alerts")
public class ExpirationAlertController {

    private final ExpirationAlertQueryService expirationAlertQueryService;
    private final AuthenticatedUserContextFacade authenticatedUserContextFacade;

    public ExpirationAlertController(ExpirationAlertQueryService expirationAlertQueryService,
                                     AuthenticatedUserContextFacade authenticatedUserContextFacade) {
        this.expirationAlertQueryService = expirationAlertQueryService;
        this.authenticatedUserContextFacade = authenticatedUserContextFacade;
    }

    @GetMapping
    @Operation(summary = "Get expiration alerts",
            description = "Retrieves the near-expiration alerts of the authenticated owner with their status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of expiration alerts"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<List<ExpirationAlertResource>> getAll() {
        try {
            var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
            var alerts = expirationAlertQueryService.handle(new GetAllExpirationAlertsQuery(ownerId));
            var resources = alerts.stream()
                    .map(ExpirationAlertResourceFromEntityAssembler::toResource)
                    .toList();
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
