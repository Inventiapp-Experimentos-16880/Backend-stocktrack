package com.inventiapp.stocktrack;

import com.inventiapp.stocktrack.sales.application.outboundservices.acl.ExternalInventoryService;
import com.inventiapp.stocktrack.sales.domain.model.aggregates.Sale;
import com.inventiapp.stocktrack.sales.domain.model.commands.CreateSaleCommand;
import com.inventiapp.stocktrack.sales.domain.model.commands.SaleDetailItem;
import com.inventiapp.stocktrack.sales.domain.model.queries.GetAllSalesQuery;
import com.inventiapp.stocktrack.sales.domain.model.queries.GetSaleByIdQuery;
import com.inventiapp.stocktrack.sales.domain.services.SaleCommandService;
import com.inventiapp.stocktrack.sales.domain.services.SaleQueryService;
import com.inventiapp.stocktrack.sales.interfaces.rest.SalesController;
import com.inventiapp.stocktrack.sales.interfaces.rest.transform.CreateSaleCommandFromResourceAssembler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SalesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SaleCommandService saleCommandService;

    @Mock
    private SaleQueryService saleQueryService;

    @Mock
    private AuthenticatedUserContextFacade authenticatedUserContextFacade;

    // ExternalInventoryService used by the assembler (we'll instantiate assembler with this mock)
    @Mock
    private ExternalInventoryService externalInventoryService;

    @InjectMocks
    private SalesController salesController;

    @BeforeEach
    void setup() {
        // Ensure the assembler's static inventoryService is initialized with our mock
        new CreateSaleCommandFromResourceAssembler(externalInventoryService);

        mockMvc = MockMvcBuilders.standaloneSetup(salesController).build();
    }

    @Test
    void createSale_shouldReturn201_andBodyWithSale() throws Exception {
        // Arrange
        long ownerId = 11L;
        long staffUserId = 5L;
        long productId = 1L;

        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        // Mock external inventory checks used by the assembler
        when(externalInventoryService.checkProductExists(productId)).thenReturn(true);
        when(externalInventoryService.getProductUnitPrice(productId)).thenReturn(10.0);

        // When command handler is called, return a new sale id
        long createdSaleId = 999L;
        when(saleCommandService.handle(any())).thenReturn(createdSaleId);

        // Build a Sale entity to be returned by the query service
        List<SaleDetailItem> details = new ArrayList<>();
        details.add(new SaleDetailItem(productId, 2, 10.0)); // total 20.0
        CreateSaleCommand createdCmd = new CreateSaleCommand(staffUserId, 20.0, details, ownerId);
        Sale sale = new Sale(createdCmd);

        when(saleQueryService.handle(new GetSaleByIdQuery(createdSaleId, ownerId))).thenReturn(Optional.of(sale));
        // As GetSaleByIdQuery equals uses record equality, but since we create a new instance above, better to stub with any(...)
        when(saleQueryService.handle(any(GetSaleByIdQuery.class))).thenReturn(Optional.of(sale));

        String payload = """
                {
                  "staffUserId": 5,
                  "products": [
                    { "productId": 1, "quantity": 2 }
                  ],
                  "kits": []
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("20.0"))); // total amount appears in response

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(saleCommandService, times(1)).handle(any());
        verify(saleQueryService, atLeastOnce()).handle(any(GetSaleByIdQuery.class));
    }

    @Test
    void getSaleById_shouldReturn200_andBody() throws Exception {
        // Arrange
        long ownerId = 22L;
        long saleId = 55L;
        long staffUserId = 7L;
        long productId = 2L;

        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        List<SaleDetailItem> details = List.of(new SaleDetailItem(productId, 1, 15.0)); // total 15.0
        CreateSaleCommand cmd = new CreateSaleCommand(staffUserId, 15.0, details, ownerId);
        Sale sale = new Sale(cmd);

        when(saleQueryService.handle(any(GetSaleByIdQuery.class))).thenReturn(Optional.of(sale));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sales/{id}", saleId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("15.0")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(saleQueryService, times(1)).handle(any(GetSaleByIdQuery.class));
    }

    @Test
    void getAllSales_shouldReturn200_andList() throws Exception {
        // Arrange
        long ownerId = 33L;
        long staffUserId = 8L;
        long productId = 3L;

        when(authenticatedUserContextFacade.getCurrentOwnerId()).thenReturn(ownerId);

        List<SaleDetailItem> details = List.of(new SaleDetailItem(productId, 4, 2.5)); // total 10.0
        CreateSaleCommand cmd = new CreateSaleCommand(staffUserId, 10.0, details, ownerId);
        Sale sale = new Sale(cmd);

        when(saleQueryService.handle(any(GetAllSalesQuery.class))).thenReturn(List.of(sale));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sales").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("10.0")));

        verify(authenticatedUserContextFacade, times(1)).getCurrentOwnerId();
        verify(saleQueryService, times(1)).handle(any(GetAllSalesQuery.class));
    }
}