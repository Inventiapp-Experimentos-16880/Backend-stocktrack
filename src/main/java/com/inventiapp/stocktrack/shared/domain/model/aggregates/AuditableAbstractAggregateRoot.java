package com.inventiapp.stocktrack.shared.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner ID for multi-tenant logical isolation.
     * Immutable: set at creation and cannot be changed after persistence.
     * All data must be filtered by the authenticated user's ownerId.
     */
    @Column(nullable = false, updatable = false)
    private Long ownerId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;

    /**
     * Protected method to set ownerId during entity creation.
     * This should only be called before the entity is persisted.
     *
     * @param ownerId The owner ID for multi-tenant isolation
     */
    protected void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Registers a domain event
     * @param event The domain event to register
     */
    public void addDomainEvent(Object event) {
        super.registerEvent(event);
    }
}
