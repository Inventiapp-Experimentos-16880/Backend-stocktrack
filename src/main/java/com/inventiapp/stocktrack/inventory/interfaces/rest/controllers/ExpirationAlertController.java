package com.inventiapp.stocktrack.inventory.interfaces.rest.controllers;

import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
import com.inventiapp.stocktrack.inventory.domain.exceptions.AlertAlreadyResolvedException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.BatchNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ExpirationAlertNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllExpirationAlertsQuery;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.ExpirationAlertResource;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.RegisterMitigationActionResource;
import com.inventiapp.stocktrack.inventory.interfaces.rest.transform.ExpirationAlertResourceFromEntityAssembler;
import com.inventiapp.stocktrack.inventory.interfaces.rest.transform.RegisterMitigationActionCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final ExpirationAlertCommandService expirationAlertCommandService;
    private final AuthenticatedUserContextFacade authenticatedUserContextFacade;

    public ExpirationAlertController(ExpirationAlertQueryService expirationAlertQueryService,
                                     ExpirationAlertCommandService expirationAlertCommandService,
                                     AuthenticatedUserContextFacade authenticatedUserContextFacade) {
        this.expirationAlertQueryService = expirationAlertQueryService;
        this.expirationAlertCommandService = expirationAlertCommandService;
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

    @PostMapping(value = "/{alertId}/actions", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a mitigation action",
            description = "Registers a mitigation action (liquidation/return) for a near-expiration alert, " +
                    "marks it as resolved and decreases the associated batch's stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitigation action registered, alert resolved"),
            @ApiResponse(responseCode = "400", description = "Invalid action type or quantity"),
            @ApiResponse(responseCode = "404", description = "Alert (or its batch) not found"),
            @ApiResponse(responseCode = "409", description = "Alert already resolved")})
    public ResponseEntity<ExpirationAlertResource> registerAction(
            @PathVariable Long alertId,
            @RequestBody RegisterMitigationActionResource resource) {
        try {
            var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
            var command = RegisterMitigationActionCommandFromResourceAssembler
                    .toCommandFromResource(alertId, resource, ownerId);
            ExpirationAlert resolved = expirationAlertCommandService.handle(command);
            return ResponseEntity.ok(ExpirationAlertResourceFromEntityAssembler.toResource(resolved));
        } catch (AlertAlreadyResolvedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ExpirationAlertNotFoundException | BatchNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
