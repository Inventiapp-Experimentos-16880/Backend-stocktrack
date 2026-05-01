package com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product aggregate.
 * Provides basic CRUD operations and additional query methods if needed.
 * All queries are automatically filtered by ownerId for multi-tenant isolation.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Backward-compatible check used by existing services.
     * @param name product name
     * @param providerId provider id
     * @return true if exists
     */
    boolean existsByNameAndProviderId(String name, String providerId);

    /**
     * Check if a product exists by name and providerId within an owner's data
     * @param name product name
     * @param providerId provider id
     * @param ownerId owner ID for multi-tenant isolation
     * @return true if exists
     */
    boolean existsByNameAndProviderIdAndOwnerId(String name, String providerId, Long ownerId);

    /**
     * Find a product by ID within an owner's data
     * @param id product ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional containing the product if found
     */
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.ownerId = :ownerId")
    Optional<Product> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Find all products for an owner
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of products
     */
    @Query("SELECT p FROM Product p WHERE p.ownerId = :ownerId ORDER BY p.id")
    List<Product> findAllByOwnerId(@Param("ownerId") Long ownerId);
}
