package com.inventiapp.stocktrack.subscriptions.domain.model.entities;

import com.inventiapp.stocktrack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan extends AuditableAbstractAggregateRoot<Plan> {
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private String billingInterval; // e.g., "month", "year"
    private String stripePriceId;
    private int maxRecipes;
    private int maxKits;
    private int maxDevices;

    public void assignOwnerId(Long ownerId) {
        super.setOwnerId(ownerId);
    }
}
