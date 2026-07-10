package com.inventiapp.stocktrack.subscriptions.domain.repositories;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByStripeCustomerId(String stripeCustomerId);
    Optional<Account> findByOwnerId(Long ownerId);
}
