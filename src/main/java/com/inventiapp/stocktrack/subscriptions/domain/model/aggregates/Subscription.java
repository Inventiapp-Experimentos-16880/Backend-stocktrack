package com.inventiapp.stocktrack.subscriptions.domain.model.aggregates;

import com.inventiapp.stocktrack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.inventiapp.stocktrack.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {
    private Long accountTableId; // referencing Account's internal id
    private String planId;
    private String stripeSubscriptionId;
    
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
    private boolean cancelAtPeriodEnd;

    public void assignOwnerId(Long ownerId) {
        super.setOwnerId(ownerId);
    }
}
