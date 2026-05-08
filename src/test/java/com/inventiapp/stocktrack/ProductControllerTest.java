package com.inventiapp.stocktrack;

import com.inventiapp.stocktrack.inventory.application.internal.commandservices.ProductCommandServiceImpl;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Product;
import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateProductCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.DeleteProductCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.UpdateProductCommand;
import com.inventiapp.stocktrack.inventory.infrastructure.internal.CategoryRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ProductRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProductCommandServiceImpl productCommandService;

    @Test
    void createProduct_shouldReturnGeneratedId_andCallSave() {
        // arrange
        CreateProductCommand createCommand = new CreateProductCommand(
                "Widget",
                "A useful widget",
                "10",
                "20",
                5,
                12.5,
                true,
                1L
        );

        // category and provider exist
        when(categoryRepository.existsById(10L)).thenReturn(true);
        when(providerRepository.existsById(20L)).thenReturn(true);

        when(productRepository.existsByNameAndProviderIdAndOwnerId("Widget", "20", 1L)).thenReturn(false);

        // simulate saved entity with DB id
        Product savedMock = mock(Product.class);
        when(savedMock.getId()).thenReturn(100L);
        when(productRepository.save(any(Product.class))).thenReturn(savedMock);

        // act
        Long resultId = productCommandService.handle(createCommand);

        // assert
        assertNotNull(resultId);
        assertEquals(100L, resultId);

        // verify save called with a Product whose ownerId equals 1L
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());
        Product captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(1L, captured.getOwnerId());
        assertEquals("Widget", captured.getName());
    }

    @Test
    void updateProduct_shouldSaveAndReturnOptionalWithSavedProduct() {
        // arrange
        UpdateProductCommand updateCommand = new UpdateProductCommand(
                5L,
                "Widget v2",
                "Updated description",
                "10",
                "20",
                3,
                15.0,
                true,
                1L
        );

        // mock existing product found by repository
        Product existingMock = mock(Product.class);
        when(productRepository.findByIdAndOwnerId(5L, 1L)).thenReturn(Optional.of(existingMock));

        // category and provider exist
        when(categoryRepository.existsById(10L)).thenReturn(true);
        when(providerRepository.existsById(20L)).thenReturn(true);

        // when saving, return the same existingMock (simulate persistence)
        when(productRepository.save(existingMock)).thenReturn(existingMock);

        // act
        Optional<Product> optionalResult = productCommandService.handle(updateCommand);

        // assert
        assertTrue(optionalResult.isPresent());
        assertEquals(existingMock, optionalResult.get());

        // verify interactions
        verify(productRepository, times(1)).findByIdAndOwnerId(5L, 1L);
        verify(existingMock, times(1)).updateProduct(updateCommand);
        verify(productRepository, times(1)).save(existingMock);
    }

    @Test
    void deleteProduct_shouldFindAndDeleteProduct() {
        // arrange
        DeleteProductCommand deleteCommand = new DeleteProductCommand(7L, 1L);

        Product existingMock = mock(Product.class);
        when(productRepository.findByIdAndOwnerId(7L, 1L)).thenReturn(Optional.of(existingMock));

        // act
        assertDoesNotThrow(() -> productCommandService.handle(deleteCommand));

        // verify
        verify(productRepository, times(1)).findByIdAndOwnerId(7L, 1L);
        verify(existingMock, times(1)).addDomainEvent(any()); // ProductDeletedEvent should be added
        verify(productRepository, times(1)).delete(existingMock);
    }
}