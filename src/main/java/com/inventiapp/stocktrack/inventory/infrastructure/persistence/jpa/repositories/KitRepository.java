package com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Kit entity.
 * 
 * @summary
 * This interface extends JpaRepository to provide CRUD operations for Kit entity.
 * It extends Spring Data JpaRepository with Kit as the entity type and Long as the ID type.
 * All queries are automatically filtered by ownerId for multi-tenant isolation.
 * @since 1.0
 */
@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {
    /**
     * Backward-compatible finder used by existing services.
     * @param name The kit name
     * @return Optional Kit
     */
    Optional<Kit> findByName(String name);

    /**
     * Backward-compatible existence check used by existing services.
     * @param name The kit name
     * @return True if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Backward-compatible finder with items used by existing services.
     * @param id The kit id
     * @return Optional Kit with items loaded
     */
    @Query("SELECT DISTINCT k FROM Kit k LEFT JOIN FETCH k.items WHERE k.id = :id")
    Optional<Kit> findByIdWithItems(@Param("id") Long id);

    /**
     * Backward-compatible list with items used by existing services.
     * @return List of kits with items loaded
     */
    @Query("SELECT DISTINCT k FROM Kit k LEFT JOIN FETCH k.items")
    List<Kit> findAllWithItems();

    /**
     * Find a kit by name for an owner.
     * @param name The kit name
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional Kit
     */
    @Query("SELECT k FROM Kit k WHERE k.name = :name AND k.ownerId = :ownerId")
    Optional<Kit> findByNameAndOwnerId(@Param("name") String name, @Param("ownerId") Long ownerId);

    /**
     * Check if a kit exists by name for an owner.
     * @param name The kit name
     * @param ownerId owner ID for multi-tenant isolation
     * @return True if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM Kit k WHERE k.name = :name AND k.ownerId = :ownerId")
    boolean existsByNameAndOwnerId(@Param("name") String name, @Param("ownerId") Long ownerId);

    /**
     * Find a kit by id with items loaded for an owner.
     * @param id The kit id
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional Kit with items loaded
     */
    @Query("SELECT DISTINCT k FROM Kit k LEFT JOIN FETCH k.items WHERE k.id = :id AND k.ownerId = :ownerId")
    Optional<Kit> findByIdWithItemsAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Find all kits with items loaded for an owner.
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of kits with items loaded
     */
    @Query("SELECT DISTINCT k FROM Kit k LEFT JOIN FETCH k.items WHERE k.ownerId = :ownerId ORDER BY k.id")
    List<Kit> findAllWithItemsByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Find a kit by ID for an owner
     * @param id kit ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional containing the kit if found
     */
    @Query("SELECT k FROM Kit k WHERE k.id = :id AND k.ownerId = :ownerId")
    Optional<Kit> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Find all kits for an owner
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of kits
     */
    @Query("SELECT k FROM Kit k WHERE k.ownerId = :ownerId ORDER BY k.id")
    List<Kit> findAllByOwnerId(@Param("ownerId") Long ownerId);
}

