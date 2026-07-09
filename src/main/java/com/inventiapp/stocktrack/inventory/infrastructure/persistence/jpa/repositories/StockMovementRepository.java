package com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the StockMovement aggregate.
 * All queries are filtered by ownerId for multi-tenant isolation.
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    /**
     * Find all movements of a batch for an owner, in chronological order.
     * A secondary sort by id keeps ordering deterministic when timestamps match.
     *
     * @param batchId batch ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return list of movements ordered by occurredAt (then id) ascending
     */
    @Query("SELECT m FROM StockMovement m WHERE m.batchId = :batchId AND m.ownerId = :ownerId " +
            "ORDER BY m.occurredAt ASC, m.id ASC")
    List<StockMovement> findByBatchIdAndOwnerIdOrderByOccurredAtAsc(@Param("batchId") Long batchId,
                                                                    @Param("ownerId") Long ownerId);
}
