package com.inventiapp.stocktrack.subscriptions.application.internal.queryservices;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Account;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.AccountRepository;
import com.inventiapp.stocktrack.subscriptions.domain.services.AccountQueryService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepository accountRepository;

    public AccountQueryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByOwnerId(Long ownerId) {
        return accountRepository.findByOwnerId(ownerId);
    }
}
