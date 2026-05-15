package com.inventiapp.stocktrack.integration;

import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.DeleteBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.exceptions.BatchNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllBatchesQuery;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetBatchByIdQuery;
import com.inventiapp.stocktrack.inventory.domain.services.BatchCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.BatchQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.controllers.BatchController;
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
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BatchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BatchCommandService batchCommandService;

    @Mock
    private BatchQueryService batchQueryService;

    @Mock
    private AuthenticatedUserContextFacade authenticatedUserContextFacade;

    @InjectMocks
    private BatchController batchController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(batchController).build();
    }

    @Test
    void createBatchTest() throws Exception {
        // Arrange
        long ownerId = 1L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        when(batchCommandService.handle(any(CreateBatchCommand.class))).thenReturn(55L);

        Date exp = new Date(); // future date
        Date rec = new Date();
        CreateBatchCommand createCmd = new CreateBatchCommand(10L, 5, exp, rec, ownerId);
        Batch batch = new Batch(createCmd);
        when(batchQueryService.handle(any(GetBatchByIdQuery.class))).thenReturn(Optional.of(batch));

        String payload = """
                {
                  "productId": 10,
                  "quantity": 5,
                  "expirationDate": %d,
                  "receptionDate": %d
                }
                """.formatted(exp.getTime(), rec.getTime());

        // Act
        var request = mockMvc.perform(post("/api/v1/batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));

        // Assert
        request.andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/batches/55")))
                .andExpect(content().string(containsString("10")))
                .andExpect(content().string(containsString("5")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(batchCommandService, times(1)).handle(any(CreateBatchCommand.class));
        verify(batchQueryService, times(1)).handle(any(GetBatchByIdQuery.class));
    }

    @Test
    void getAllBatchesTest() throws Exception {
        // Arrange
        long ownerId = 2L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        Date exp = new Date(1L);
        Date rec = new Date();
        CreateBatchCommand cmd = new CreateBatchCommand(21L, 10, exp, rec, ownerId);
        Batch b = new Batch(cmd);
        when(batchQueryService.handle(any(GetAllBatchesQuery.class))).thenReturn(List.of(b));

        // Act
        var request = mockMvc.perform(get("/api/v1/batches").accept(MediaType.APPLICATION_JSON));

        // Assert
        request.andExpect(status().isOk())
                .andExpect(content().string(containsString("21")))
                .andExpect(content().string(containsString("10")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(batchQueryService, times(1)).handle(any(GetAllBatchesQuery.class));
    }

    @Test
    void deleteBatchTest() throws Exception {
        // Arrange
        long ownerId = 3L;
        long batchId = 999L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        doThrow(new BatchNotFoundException(batchId))
                .when(batchCommandService).handle(any(DeleteBatchCommand.class));

        // Act
        var request = mockMvc.perform(delete("/api/v1/batches/{id}", batchId));

        // Assert
        request.andExpect(status().isNotFound());

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(batchCommandService, times(1)).handle(any(DeleteBatchCommand.class));
    }
}