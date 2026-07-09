package com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the ExpirationAlert aggregate.
 * All queries are filtered by ownerId for multi-tenant isolation.
 */
@Repository
public interface ExpirationAlertRepository extends JpaRepository<ExpirationAlert, Long> {

    /**
     * Idempotency guard for the daily scan: checks whether an alert with the given status
     * already exists for a batch within an owner's data.
     *
     * @param batchId batch ID
     * @param ownerId owner ID for multi-tenant isolation
     * @param status  alert status to check
     * @return true if such an alert already exists
     */
    boolean existsByBatchIdAndOwnerIdAndStatus(Long batchId, Long ownerId, AlertStatus status);

    /**
     * Find all alerts for an owner, most recently triggered first.
     *
     * @param ownerId owner ID for multi-tenant isolation
     * @return list of alerts
     */
    @Query("SELECT a FROM ExpirationAlert a WHERE a.ownerId = :ownerId ORDER BY a.triggeredAt DESC, a.id DESC")
    List<ExpirationAlert> findAllByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Find alerts for an owner filtered by status, most recently triggered first.
     *
     * @param ownerId owner ID for multi-tenant isolation
     * @param status  alert status to filter by
     * @return list of alerts
     */
    @Query("SELECT a FROM ExpirationAlert a WHERE a.ownerId = :ownerId AND a.status = :status " +
            "ORDER BY a.triggeredAt DESC, a.id DESC")
    List<ExpirationAlert> findAllByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                                    @Param("status") AlertStatus status);
}
