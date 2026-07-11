package com.inventiapp.stocktrack.subscriptions.domain.repositories;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    Optional<Subscription> findByAccountTableId(Long accountTableId);
}
