package com.inventiapp.stocktrack.subscriptions.domain.services;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Account;
import java.util.Optional;

public interface AccountQueryService {
    Optional<Account> findById(Long id);
    Optional<Account> findByOwnerId(Long ownerId);
}
