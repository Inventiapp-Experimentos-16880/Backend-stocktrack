package com.inventiapp.stocktrack.subscriptions.application.internal.commandservices;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Account;
import com.inventiapp.stocktrack.subscriptions.domain.model.commands.CreateAccountCommand;
import com.inventiapp.stocktrack.subscriptions.domain.model.valueobjects.AccountStatus;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.AccountRepository;
import com.inventiapp.stocktrack.subscriptions.domain.services.AccountCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private final AccountRepository accountRepository;

    public AccountCommandServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account handle(CreateAccountCommand command) {
        var existing = accountRepository.findByOwnerId(command.ownerId());
        if (existing.isPresent()) {
            log.info("Account already exists for ownerId: {}", command.ownerId());
            return existing.get();
        }

        Account account = Account.builder()
                .email(command.email())
                .stripeCustomerId(null)
                .currentPlanId(null)
                .status(AccountStatus.INACTIVE)
                .build();
        account.assignOwnerId(command.ownerId()); // Using assignOwnerId since it's available or setOwnerId. Wait, let's verify if setOwnerId is protected but accessible.

        Account saved = accountRepository.save(account);
        log.info("Subscription account created: id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }
}
