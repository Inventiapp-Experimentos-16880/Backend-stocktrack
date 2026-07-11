package com.inventiapp.stocktrack.subscriptions.domain.model.aggregates;

import com.inventiapp.stocktrack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.inventiapp.stocktrack.subscriptions.domain.model.valueobjects.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends AuditableAbstractAggregateRoot<Account> {
    private String email;
    private String stripeCustomerId;
    
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    
    private String currentPlanId;

    public void assignOwnerId(Long ownerId) {
        super.setOwnerId(ownerId);
    }
}
