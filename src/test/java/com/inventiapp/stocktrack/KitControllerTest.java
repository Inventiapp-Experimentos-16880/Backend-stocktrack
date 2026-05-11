package com.inventiapp.stocktrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateKitCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.DeleteKitCommand;
import com.inventiapp.stocktrack.inventory.domain.exceptions.KitNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Kit;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllKitsQuery;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetKitByIdQuery;
import com.inventiapp.stocktrack.inventory.domain.services.KitCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.KitQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.controllers.KitController;
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

import java.util.Collections;
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
class KitControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private KitCommandService kitCommandService;

    @Mock
    private KitQueryService kitQueryService;

    @Mock
    private AuthenticatedUserContextFacade authenticatedUserContextFacade;

    @InjectMocks
    private KitController kitController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(kitController).build();
    }

    @Test
    void createKit_shouldReturn201_andBodyWithKit() throws Exception {
        // Arrange
        long ownerId = 1L;
        var kitItem = Collections.singletonList(new CreateKitCommand.KitItemCommand(1L, 2, 0.3));
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        CreateKitCommand createCmd = new CreateKitCommand("Starter Kit", kitItem, ownerId);
        Kit kit = new Kit(createCmd);

        when(kitCommandService.handle(any(CreateKitCommand.class))).thenReturn(Optional.of(kit));

        String payload = """
                {
                  "name": "Starter Kit",
                  "items": [
                    { "productId": 1, "quantity": 2, "price": 0.3 }
                  ]
                }
                """;

        // Act
        var request = mockMvc.perform(post("/api/v1/kits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));

        // Assert
        request.andExpect(status().isCreated())
                .andExpect(content().string(containsString("Starter Kit")))
                .andExpect(content().string(containsString("productId")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(kitCommandService, times(1)).handle(any(CreateKitCommand.class));
    }

    @Test
    void getAllKits_shouldReturn200_andList() throws Exception {
        // Arrange
        long ownerId = 2L;
        var kitItem = Collections.singletonList(new CreateKitCommand.KitItemCommand(1L, 2, 0.3));
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        CreateKitCommand cmd = new CreateKitCommand("Bundle", kitItem, ownerId);
        Kit k = new Kit(cmd);
        when(kitQueryService.handle(any(GetAllKitsQuery.class))).thenReturn(List.of(k));

        // Act
        var request = mockMvc.perform(get("/api/v1/kits").accept(MediaType.APPLICATION_JSON));

        // Assert
        request.andExpect(status().isOk())
                .andExpect(content().string(containsString("Bundle")))
                .andExpect(content().string(containsString("productId")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(kitQueryService, times(1)).handle(any(GetAllKitsQuery.class));
    }

    @Test
    void deleteKit_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        long ownerId = 3L;
        long kitId = 999L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        doThrow(new KitNotFoundException(kitId))
                .when(kitCommandService).handle(any(DeleteKitCommand.class));

        // Act
        var request = mockMvc.perform(delete("/api/v1/kits/{id}", kitId));

        // Assert
        request.andExpect(status().isNotFound());

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(kitCommandService, times(1)).handle(any(DeleteKitCommand.class));
    }
}