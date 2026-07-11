package com.inventiapp.stocktrack.inventory.domain.model.aggregates;

import com.inventiapp.stocktrack.inventory.domain.exceptions.AlertAlreadyResolvedException;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MitigationActionType;
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
     * Mitigation action taken to resolve the alert. Null while the alert is PENDING.
     */
    @Enumerated(EnumType.STRING)
    @Column
    private MitigationActionType actionType;

    /**
     * Quantity effectively taken out of stock by the mitigation action. Null while the alert is
     * PENDING. When the caller omits a quantity the action covers the whole batch, so this holds
     * the full quantity that was removed, never the raw request value.
     */
    @Column
    private Integer actionQuantity;

    /**
     * Timestamp when the mitigation action resolved the alert. Null while the alert is PENDING.
     */
    @Column
    private Date resolvedAt;

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

    /**
     * Resolves the alert by registering the mitigation action taken (US17 part 2).
     * Records the action, the quantity it removed from stock and its timestamp, and moves the
     * alert to RESOLVED.
     *
     * @param actionType     the mitigation action taken (liquidation/return)
     * @param actionQuantity the quantity effectively removed from the batch by the action
     * @throws IllegalArgumentException      if the action type is null or the quantity is not positive
     * @throws AlertAlreadyResolvedException if the alert is not PENDING
     */
    public void resolve(MitigationActionType actionType, Integer actionQuantity) {
        if (actionType == null) throw new IllegalArgumentException("actionType is required");
        if (actionQuantity == null || actionQuantity <= 0) {
            throw new IllegalArgumentException("actionQuantity must be a positive quantity");
        }
        if (this.status != AlertStatus.PENDING) {
            throw new AlertAlreadyResolvedException(this.getId());
        }

        this.actionType = actionType;
        this.actionQuantity = actionQuantity;
        this.resolvedAt = new Date();
        this.status = AlertStatus.RESOLVED;
    }
}
