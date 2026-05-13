package com.inventiapp.stocktrack.integration;

import com.inventiapp.stocktrack.inventory.domain.exceptions.ProviderNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Provider;
import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateProviderCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.DeleteProviderCommand;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllProvidersQuery;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetProviderByIdQuery;
import com.inventiapp.stocktrack.inventory.interfaces.rest.controllers.ProviderController;
import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
import com.inventiapp.stocktrack.inventory.domain.services.ProviderCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.ProviderQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class ProviderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProviderCommandService providerCommandService;

    @Mock
    private ProviderQueryService providerQueryService;

    @Mock
    private AuthenticatedUserContextFacade authenticatedUserContextFacade;

    @InjectMocks
    private ProviderController providerController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(providerController).build();
    }

    @Test
    void createProviderTest() throws Exception {
        // arrange
        long ownerId = 1L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        // Mock service: after create returns id and query returns provider
        when(providerCommandService.handle((CreateProviderCommand) any())).thenReturn(10L);

        // Create a Provider aggregate to be returned by query service
        CreateProviderCommand createdCmd = new CreateProviderCommand("Pedro", "Rios", "978567485", "pedro@hotmail.com", "98765432101", ownerId);
        Provider provider = new Provider(createdCmd); // build a real aggregate for response
        when(providerQueryService.handle(any(GetProviderByIdQuery.class))).thenReturn(Optional.of(provider));

        // request body (CreateProviderResource shape)
        String payload = """
                {
                  "firstName": "Pedro",
                  "lastName": "Rios",
                  "phoneNumber": "978567485",
                  "email": "pedro@hotmail.com",
                  "ruc": "98765432101"
                }
                """;

        // act & assert
        mockMvc.perform(post("/api/v1/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/providers/10")))
                .andExpect(content().string(containsString("Pedro")))
                .andExpect(content().string(containsString("Rios")));

        // verify interactions
        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(providerCommandService, times(1)).handle((CreateProviderCommand) any());
        verify(providerQueryService, times(1)).handle(any(GetProviderByIdQuery.class));
    }

    @Test
    void getAllProvidersTest() throws Exception {
        // arrange
        long ownerId = 2L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        // create simple provider
        CreateProviderCommand cmd = new CreateProviderCommand("Ana", "Lopez", "987654321", "ana@example.com", "11122233344", ownerId);
        Provider p = new Provider(cmd);

        when(providerQueryService.handle(any(GetAllProvidersQuery.class))).thenReturn(List.of(p));

        // act & assert
        mockMvc.perform(get("/api/v1/providers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana")))
                .andExpect(content().string(containsString("Lopez")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(providerQueryService, times(1)).handle(any(GetAllProvidersQuery.class));
    }

    @Test
    void deleteProviderTest() throws Exception {
        // arrange
        long ownerId = 3L;
        long providerId = 999L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        // Simulate service throwing ProviderNotFoundException
        doThrow(new ProviderNotFoundException(providerId))
                .when(providerCommandService).handle(any(DeleteProviderCommand.class));

        // act & assert
        mockMvc.perform(delete("/api/v1/providers/{id}", providerId))
                .andExpect(status().isNotFound());

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(providerCommandService, times(1)).handle(any(DeleteProviderCommand.class));
    }
}