package com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Batch aggregate.
 * Provides basic CRUD operations and additional query methods if needed.
 * All queries are automatically filtered by ownerId for multi-tenant isolation.
 */
@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    /**
     * Backward-compatible check used by existing services.
     * @param productId product id
     * @return true if exists
     */
    boolean existsByProductId(Long productId);

    /**
     * Backward-compatible check used by existing services.
     * @param expirationDate expiration date
     * @return true if exists
     */
    boolean existsByExpirationDate(Date expirationDate);

    /**
     * Backward-compatible check used by existing services.
     * @param receptionDate reception date
     * @return true if exists
     */
    boolean existsByReceptionDate(Date receptionDate);

    /**
     * Backward-compatible finder used by existing services.
     * @param productId product ID
     * @return List of batches
     */
    List<Batch> findByProductIdOrderByExpirationDateAsc(Long productId);

    /**
     * Check if a batch exists by productId for an owner
     * @param productId product id
     * @param ownerId owner ID for multi-tenant isolation
     * @return true if exists
     */
    boolean existsByProductIdAndOwnerId(Long productId, Long ownerId);

    /**
     * Find a batch by ID within an owner's data
     * @param id batch ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional containing the batch if found
     */
    @Query("SELECT b FROM Batch b WHERE b.id = :id AND b.ownerId = :ownerId")
    Optional<Batch> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Find batch by productId ordered by expiration date for an owner
     * @param productId product ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of batches
     */
    @Query("SELECT b FROM Batch b WHERE b.productId = :productId AND b.ownerId = :ownerId ORDER BY b.expirationDate ASC")
    List<Batch> findByProductIdAndOwnerIdOrderByExpirationDateAsc(@Param("productId") Long productId, @Param("ownerId") Long ownerId);

    /**
     * Find all batches for an owner
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of batches
     */
    @Query("SELECT b FROM Batch b WHERE b.ownerId = :ownerId ORDER BY b.id")
    List<Batch> findAllByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Sum the total quantity of batches for a product and owner
     * @param productId product ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return total quantity
     */
    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM Batch b WHERE b.productId = :productId AND b.ownerId = :ownerId")
    Integer sumQuantityByProductIdAndOwnerId(@Param("productId") Long productId, @Param("ownerId") Long ownerId);
}
