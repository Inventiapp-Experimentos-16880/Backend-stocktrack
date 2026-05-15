package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.sales.application.internal.commandservices.SaleCommandServiceImpl;
import com.inventiapp.stocktrack.sales.domain.model.aggregates.Sale;
import com.inventiapp.stocktrack.sales.domain.model.commands.CreateSaleCommand;
import com.inventiapp.stocktrack.sales.domain.model.commands.SaleDetailItem;
import com.inventiapp.stocktrack.sales.infrastructure.persistence.jpa.repositories.SaleRepository;
import com.inventiapp.stocktrack.sales.application.outboundservices.acl.ExternalInventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ExternalInventoryService externalInventoryService;

    @InjectMocks
    private SaleCommandServiceImpl saleCommandService;

    @Test
    void createSale_shouldReturnGeneratedId_andCallDecreaseStock() throws Exception {
        // arrange
        SaleDetailItem item = new SaleDetailItem(1L, 2, 10.0);
        CreateSaleCommand command = new CreateSaleCommand(1L, 20.0, List.of(item), 1L);

        when(externalInventoryService.checkProductExists(1L)).thenReturn(true);

        // when saving, set id on the passed Sale instance and return it
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale s = invocation.getArgument(0);
            setIdViaReflection(s, 123L);
            return s;
        });

        // act
        Long resultId = saleCommandService.handle(command);

        // assert
        assertNotNull(resultId);
        assertEquals(123L, resultId);

        // verify interactions
        verify(saleRepository, times(1)).save(any(Sale.class));
        verify(externalInventoryService, times(1)).decreaseStockForSale(any(Sale.class));
    }

    @Test
    void createSale_whenProductMissing_shouldThrowIllegalArgumentException() {
        // arrange
        SaleDetailItem item = new SaleDetailItem(99L, 1, 5.0);
        CreateSaleCommand command = new CreateSaleCommand(1L, 5.0, List.of(item), 1L);

        // product does NOT exist
        when(externalInventoryService.checkProductExists(99L)).thenReturn(false);

        // act & assert
        assertThrows(IllegalArgumentException.class, () -> saleCommandService.handle(command));

        // verify save never called
        verify(saleRepository, never()).save(any(Sale.class));
        verify(externalInventoryService, never()).decreaseStockForSale(any(Sale.class));
    }

    @Test
    void createSale_shouldCallSaveBeforeDecreaseStock() throws Exception {
        // arrange
        SaleDetailItem item = new SaleDetailItem(2L, 1, 7.5);
        CreateSaleCommand command = new CreateSaleCommand(2L, 7.5, List.of(item), 2L);

        when(externalInventoryService.checkProductExists(2L)).thenReturn(true);

        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale s = invocation.getArgument(0);
            setIdViaReflection(s, 555L);
            return s;
        });

        // act
        Long id = saleCommandService.handle(command);

        // assert
        assertEquals(555L, id);

        // verify order: save -> decreaseStockForSale
        InOrder inOrder = inOrder(saleRepository, externalInventoryService);
        inOrder.verify(saleRepository).save(any(Sale.class));
        inOrder.verify(externalInventoryService).decreaseStockForSale(any(Sale.class));
    }

    // helper to set private id field via reflection; walks superclasses to find "id"
    private static void setIdViaReflection(Object target, Long id) throws Exception {
        Class<?> cls = target.getClass();
        Field idField = null;
        while (cls != null) {
            try {
                idField = cls.getDeclaredField("id");
                break;
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            }
        }
        if (idField == null) {
            throw new NoSuchFieldException("No 'id' field found on target or its superclasses");
        }
        idField.setAccessible(true);
        idField.set(target, id);
    }
}