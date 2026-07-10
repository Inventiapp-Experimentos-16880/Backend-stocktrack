package com.inventiapp.stocktrack.integration;

import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.StockMovement;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RecordStockMovementCommand;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetStockMovementsByBatchIdQuery;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MovementType;
import com.inventiapp.stocktrack.inventory.domain.services.StockMovementQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.controllers.BatchMovementsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Covers the US18 acceptance criteria for the batch movement history timeline endpoint.
 */
@ExtendWith(MockitoExtension.class)
class BatchMovementsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockMovementQueryService stockMovementQueryService;

    @Mock
    private AuthenticatedUserContextFacade authenticatedUserContextFacade;

    @InjectMocks
    private BatchMovementsController batchMovementsController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(batchMovementsController).build();
    }

    private StockMovement movement(long productId, long batchId, MovementType type, int quantity, Date occurredAt, long ownerId) {
        return new StockMovement(new RecordStockMovementCommand(productId, batchId, type, quantity, occurredAt, ownerId));
    }

    @Test
    void returnsEntradaAndSalidaInChronologicalOrder() throws Exception {
        // Arrange: a batch with an initial entry and a later exit, returned in chronological order.
        long ownerId = 5L;
        long productId = 10L;
        long batchId = 30L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        Date entryDate = new Date(1_000_000L);
        Date exitDate = new Date(2_000_000L);
        // Returned in chronological order, as the query service / repository would provide.
        StockMovement entrada = movement(productId, batchId, MovementType.ENTRADA, 100, entryDate, ownerId);
        StockMovement salida = movement(productId, batchId, MovementType.SALIDA, 40, exitDate, ownerId);

        when(stockMovementQueryService.handle(any(GetStockMovementsByBatchIdQuery.class)))
                .thenReturn(List.of(entrada, salida));

        // Act
        var request = mockMvc.perform(get("/api/v1/batches/{batchId}/movements", batchId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert: both movements present, ENTRADA before SALIDA (chronological order preserved).
        request.andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("ENTRADA"))
                .andExpect(jsonPath("$[0].quantity").value(100))
                .andExpect(jsonPath("$[1].type").value("SALIDA"))
                .andExpect(jsonPath("$[1].quantity").value(40));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(stockMovementQueryService, times(1)).handle(any(GetStockMovementsByBatchIdQuery.class));
    }

    @Test
    void returnsOnlyInitialEntradaWhenNoExitsRecorded() throws Exception {
        // Arrange: a batch that only registered its initial reception.
        long ownerId = 7L;
        long productId = 11L;
        long batchId = 31L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        StockMovement entrada = movement(productId, batchId, MovementType.ENTRADA, 50, new Date(1_000_000L), ownerId);
        when(stockMovementQueryService.handle(any(GetStockMovementsByBatchIdQuery.class)))
                .thenReturn(List.of(entrada));

        // Act
        var request = mockMvc.perform(get("/api/v1/batches/{batchId}/movements", batchId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert: exactly one movement, of type ENTRADA.
        request.andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("ENTRADA"))
                .andExpect(jsonPath("$[0].quantity").value(50));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(stockMovementQueryService, times(1)).handle(any(GetStockMovementsByBatchIdQuery.class));
    }
}
