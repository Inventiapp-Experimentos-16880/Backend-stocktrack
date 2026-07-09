package com.inventiapp.stocktrack.inventory.domain.model.aggregates;

import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;
import com.inventiapp.stocktrack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ExpirationAlert Aggregate Root
 * <p>
 * Persisted alert stating that a batch is approaching its expiration date. Raised by the
 * daily expiration scan in PENDING status. It does not mutate the batch; it only records
 * that the batch requires attention.
 * <p>
 * The alert stays PENDING until an explicit mitigation action resolves it (US17 part 2);
 * there is no automatic expiry. Inherits id, ownerId and audit fields for multi-tenant isolation.
 */
@Entity
@Table(name = "expiration_alerts")
@Getter
@NoArgsConstructor
public class ExpirationAlert extends AuditableAbstractAggregateRoot<ExpirationAlert> {

    @Column(nullable = false)
    private Long batchId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Date expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @Column(nullable = false)
    private Date triggeredAt;

    /**
     * Creates a new ExpirationAlert from the RaiseExpirationAlertCommand in PENDING status.
     * Sets ownerId for multi-tenant isolation.
     *
     * @param command the raise expiration alert command (includes ownerId)
     */
    public ExpirationAlert(RaiseExpirationAlertCommand command) {
        if (command == null) throw new IllegalArgumentException("RaiseExpirationAlertCommand is required");

        this.batchId = command.batchId();
        this.productId = command.productId();
        this.expirationDate = command.expirationDate();
        this.triggeredAt = command.triggeredAt();
        this.status = AlertStatus.PENDING;

        // Set ownerId for multi-tenant isolation
        this.setOwnerId(command.ownerId());
    }
}
