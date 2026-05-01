package com.inventiapp.stocktrack.sales.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.sales.domain.model.aggregates.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sale aggregate.
 * All queries are automatically filtered by ownerId for multi-tenant isolation.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Find a sale by ID within an owner's data
     * @param id sale ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional containing the sale if found
     */
    @Query("SELECT s FROM Sale s WHERE s.id = :id AND s.ownerId = :ownerId")
    Optional<Sale> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Find all sales for an owner
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of sales
     */
    @Query("SELECT s FROM Sale s WHERE s.ownerId = :ownerId ORDER BY s.id DESC")
    List<Sale> findAllByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Find all sales for a staff user within an owner's data
     * @param staffUserId staff user ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of sales
     */
    @Query("SELECT s FROM Sale s WHERE s.staffUserId.id = :staffUserId AND s.ownerId = :ownerId ORDER BY s.id DESC")
    List<Sale> findByStaffUserIdAndOwnerId(@Param("staffUserId") Long staffUserId, @Param("ownerId") Long ownerId);
}
