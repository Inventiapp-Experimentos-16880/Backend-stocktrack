package com.inventiapp.stocktrack.subscriptions.domain.repositories;

import com.inventiapp.stocktrack.subscriptions.domain.model.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByStripePriceId(String stripePriceId);
}
