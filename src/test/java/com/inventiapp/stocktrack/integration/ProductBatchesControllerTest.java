package com.inventiapp.stocktrack.integration;

import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllBatchesByProductIdQuery;
import com.inventiapp.stocktrack.inventory.domain.services.BatchQueryService;
import com.inventiapp.stocktrack.inventory.domain.services.ProductQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.controllers.ProductBatchesController;
import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductBatchesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductQueryService productQueryService;

    @Mock
    private BatchQueryService batchQueryService;

    @Mock
    private AuthenticatedUserContextFacade authenticatedUserContextFacade;

    @InjectMocks
    private ProductBatchesController productBatchesController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productBatchesController).build();
    }

    @Test
    void getAllBatchesTest() throws Exception {
        // Arrange
        long ownerId = 5L;
        long productId = 10L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        Date reception = new Date();
        Date expiration = new Date(System.currentTimeMillis() + 86400000L); // +1 day

        CreateBatchCommand cmd = new CreateBatchCommand(productId, 100, expiration, reception, ownerId);
        Batch batch = new Batch(cmd);

        when(batchQueryService.handle(any(GetAllBatchesByProductIdQuery.class))).thenReturn(List.of(batch));

        // Act
        var request = mockMvc.perform(get("/api/v1/products/{productId}/batches", productId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        request.andExpect(status().isOk())
                .andExpect(content().string(containsString(String.valueOf(productId))))
                .andExpect(content().string(containsString(String.valueOf(batch.getQuantity()))));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(batchQueryService, times(1)).handle(any(GetAllBatchesByProductIdQuery.class));
    }

    @Test
    void getAllBatchesByProductIdTest() throws Exception {
        // Arrange
        long ownerId = 7L;
        long productId = 20L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        when(batchQueryService.handle(any(GetAllBatchesByProductIdQuery.class)))
                .thenThrow(new IllegalArgumentException("invalid"));

        // Act
        var request = mockMvc.perform(get("/api/v1/products/{productId}/batches", productId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        request.andExpect(status().isBadRequest());

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(batchQueryService, times(1)).handle(any(GetAllBatchesByProductIdQuery.class));
    }
}