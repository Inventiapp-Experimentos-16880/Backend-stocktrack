package com.inventiapp.stocktrack.inventory.interfaces.rest.controllers;

import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetStockMovementsByBatchIdQuery;
import com.inventiapp.stocktrack.inventory.domain.services.StockMovementQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.StockMovementResource;
import com.inventiapp.stocktrack.inventory.interfaces.rest.transform.StockMovementResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller exposing the chronological movement history (ENTRADA/SALIDA) of a batch.
 */
@RestController
@RequestMapping(value = "/api/v1/batches/{batchId}/movements", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Batches", description = "Endpoints for batch movement history")
public class BatchMovementsController {

    private final StockMovementQueryService stockMovementQueryService;
    private final AuthenticatedUserContextFacade authenticatedUserContextFacade;

    public BatchMovementsController(StockMovementQueryService stockMovementQueryService,
                                    AuthenticatedUserContextFacade authenticatedUserContextFacade) {
        this.stockMovementQueryService = stockMovementQueryService;
        this.authenticatedUserContextFacade = authenticatedUserContextFacade;
    }

    @GetMapping
    @Operation(summary = "Get movement history by batch id",
            description = "Retrieves the chronological ENTRADA/SALIDA movements of a batch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of batch movements"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<List<StockMovementResource>> getMovementsByBatchId(@PathVariable Long batchId) {
        try {
            var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
            var query = new GetStockMovementsByBatchIdQuery(batchId, ownerId);
            var movements = stockMovementQueryService.handle(query);
            var resources = movements.stream()
                    .map(StockMovementResourceFromEntityAssembler::toResource)
                    .toList();
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
