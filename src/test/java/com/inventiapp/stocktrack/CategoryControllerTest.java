package com.inventiapp.stocktrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Category;
import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateCategoryCommand;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllCategoriesQuery;
import com.inventiapp.stocktrack.inventory.domain.services.CategoryCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.CategoryQueryService;
import com.inventiapp.stocktrack.inventory.interfaces.rest.controllers.CategoryController;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CategoryCommandService categoryCommandService;

    @Mock
    private CategoryQueryService categoryQueryService;

    @Mock
    private AuthenticatedUserContextFacade authenticatedUserContextFacade;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void createCategory_shouldReturn201_andBodyWithCategory() throws Exception {
        // Arrange
        long ownerId = 1L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        // Simular que el servicio devuelve Optional.of(category)
        CreateCategoryCommand cmd = new CreateCategoryCommand("Electronics", "Devices", ownerId);
        Category created = new Category(cmd);
        when(categoryCommandService.handle(any(CreateCategoryCommand.class))).thenReturn(Optional.of(created));

        String payload = """
                {
                  "name": "Electronics",
                  "description": "Devices"
                }
                """;

        // Act
        var request = mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));

        // Assert
        request.andExpect(status().isCreated())
                .andExpect(content().string(containsString("Electronics")))
                .andExpect(content().string(containsString("Devices")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(categoryCommandService, times(1)).handle(any(CreateCategoryCommand.class));
    }

    @Test
    void getAllCategories_shouldReturn200_andList() throws Exception {
        // Arrange
        long ownerId = 2L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        CreateCategoryCommand cmd = new CreateCategoryCommand("Food", "Groceries", ownerId);
        Category c = new Category(cmd);
        when(categoryQueryService.handle(any(GetAllCategoriesQuery.class))).thenReturn(List.of(c));

        // Act
        var request = mockMvc.perform(get("/api/v1/categories")
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        request.andExpect(status().isOk())
                .andExpect(content().string(containsString("Food")))
                .andExpect(content().string(containsString("Groceries")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(categoryQueryService, times(1)).handle(any(GetAllCategoriesQuery.class));
    }

    @Test
    void createCategory_whenNameAlreadyExists_shouldReturn400() throws Exception {
        // Arrange
        long ownerId = 3L;
        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        // Simular que el servicio lanza IllegalArgumentException (por nombre duplicado)
        when(categoryCommandService.handle(any(CreateCategoryCommand.class)))
                .thenThrow(new IllegalArgumentException("Category exists"));

        String payload = """
                {
                  "name": "Duplicate",
                  "description": "X"
                }
                """;

        // Act
        var request = mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));

        // Assert
        request.andExpect(status().isBadRequest());

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(categoryCommandService, times(1)).handle(any(CreateCategoryCommand.class));
    }
}