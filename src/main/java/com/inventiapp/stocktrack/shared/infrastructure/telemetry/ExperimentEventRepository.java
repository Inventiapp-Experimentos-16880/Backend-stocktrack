package com.inventiapp.stocktrack.shared.infrastructure.telemetry;

import com.inventiapp.stocktrack.shared.infrastructure.telemetry.model.ExperimentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the generic ExperimentEvent telemetry record.
 */
@Repository
public interface ExperimentEventRepository extends JpaRepository<ExperimentEvent, Long> {

    /**
     * Find telemetry events of a given type for an owner.
     *
     * @param ownerId   owner ID for multi-tenant isolation
     * @param eventType event type identifier
     * @return list of matching events
     */
    List<ExperimentEvent> findByOwnerIdAndEventType(Long ownerId, String eventType);
}
