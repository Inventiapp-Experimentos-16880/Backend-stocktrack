package com.inventiapp.stocktrack.integration;

import com.inventiapp.stocktrack.inventory.application.internal.queryservices.ProductQueryServiceImpl;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Product;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetProductsByNameQuery;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchQueryTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductQueryServiceImpl productQueryService;

    @Test
    void searchByNamePartialMatchTest() {
        // arrange
        Long ownerId = 1L;
        String searchTerm = "widg";
        GetProductsByNameQuery query = new GetProductsByNameQuery(ownerId, searchTerm);

        Product p1 = mock(Product.class);
        when(p1.getName()).thenReturn("Widget A");
        Product p2 = mock(Product.class);
        when(p2.getName()).thenReturn("Super Widget");

        when(productRepository.findAllByOwnerIdAndNameContaining(ownerId, searchTerm))
                .thenReturn(List.of(p1, p2));

        // act
        List<Product> results = productQueryService.handle(query);

        // assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Widget A", results.get(0).getName());
        assertEquals("Super Widget", results.get(1).getName());

        verify(productRepository, times(1)).findAllByOwnerIdAndNameContaining(ownerId, searchTerm);
    }
}
