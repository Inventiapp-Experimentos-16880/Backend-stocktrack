package com.inventiapp.stocktrack.inventory.infrastructure.internal;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Category entity.
 * @summary
 * This interface extends JpaRepository to provide CRUD operations for Category entity.
 * It extends Spring Data JpaRepository with Category as the entity type and Long as the ID type.
 * All queries are automatically filtered by ownerId for multi-tenant isolation.
 * @since 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Backward-compatible finder used by existing services.
     * @param name The category name
     * @return Optional Category
     */
    Optional<Category> findByName(String name);

    /**
     * Backward-compatible existence check used by existing services.
     * @param name The category name
     * @return True if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find a category by name for an owner.
     * @param name The category name
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional Category
     */
    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.ownerId = :ownerId")
    Optional<Category> findByNameAndOwnerId(@Param("name") String name, @Param("ownerId") Long ownerId);

    /**
     * Check if a category exists by name for an owner.
     * @param name The category name
     * @param ownerId owner ID for multi-tenant isolation
     * @return True if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.name = :name AND c.ownerId = :ownerId")
    boolean existsByNameAndOwnerId(@Param("name") String name, @Param("ownerId") Long ownerId);

    /**
     * Find a category by ID for an owner
     * @param id category ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional containing the category if found
     */
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.ownerId = :ownerId")
    Optional<Category> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Find all categories for an owner
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of categories
     */
    @Query("SELECT c FROM Category c WHERE c.ownerId = :ownerId ORDER BY c.id")
    List<Category> findAllByOwnerId(@Param("ownerId") Long ownerId);
}



