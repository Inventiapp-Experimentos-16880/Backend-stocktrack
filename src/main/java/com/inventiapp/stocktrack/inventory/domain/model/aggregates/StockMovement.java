package com.inventiapp.stocktrack.inventory.domain.model.aggregates;

import com.inventiapp.stocktrack.inventory.domain.model.commands.RecordStockMovementCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MovementType;
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
 * StockMovement Aggregate Root
 * <p>
 * Immutable audit record of an inventory movement (ENTRADA/SALIDA) against a batch.
 * Recorded in addition to the batch stock changes; it never mutates batch quantity itself.
 * Inherits id, ownerId and audit fields for multi-tenant isolation.
 */
@Entity
@Table(name = "stock_movements")
@Getter
@NoArgsConstructor
public class StockMovement extends AuditableAbstractAggregateRoot<StockMovement> {

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long batchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Date occurredAt;

    /**
     * Creates a new StockMovement from the RecordStockMovementCommand.
     * Sets ownerId for multi-tenant isolation.
     *
     * @param command the record movement command (includes ownerId)
     */
    public StockMovement(RecordStockMovementCommand command) {
        if (command == null) throw new IllegalArgumentException("RecordStockMovementCommand is required");

        this.productId = command.productId();
        this.batchId = command.batchId();
        this.type = command.type();
        this.quantity = command.quantity();
        this.occurredAt = command.occurredAt();

        // Set ownerId for multi-tenant isolation
        this.setOwnerId(command.ownerId());
    }
}
