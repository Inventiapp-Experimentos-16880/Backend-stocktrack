package com.inventiapp.stocktrack.shared.infrastructure.telemetry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Generic, append-only telemetry event used to track experiment funnels (tracking plan 8.2.8).
 * <p>
 * This is deliberately an infrastructure record, not a domain aggregate: it is written by
 * telemetry publishers and is decoupled from the domain model. The payload holds a small JSON
 * document with event-specific fields.
 */
@Entity
@Table(name = "experiment_events")
@Getter
@NoArgsConstructor
public class ExperimentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner ID for multi-tenant logical isolation.
     */
    @Column(nullable = false)
    private Long ownerId;

    /**
     * Event type identifier from the tracking plan (e.g. "batch_alert_triggered").
     */
    @Column(nullable = false)
    private String eventType;

    /**
     * Identifier of the entity the event refers to (e.g. the alert id).
     */
    @Column
    private Long entityId;

    /**
     * Event-specific data serialized as a JSON string.
     */
    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Date occurredAt;

    public ExperimentEvent(Long ownerId, String eventType, Long entityId, String payload, Date occurredAt) {
        this.ownerId = ownerId;
        this.eventType = eventType;
        this.entityId = entityId;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }
}
