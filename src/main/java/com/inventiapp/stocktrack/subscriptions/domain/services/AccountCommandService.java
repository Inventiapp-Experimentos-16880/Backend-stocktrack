package com.inventiapp.stocktrack.subscriptions.domain.services;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Account;
import com.inventiapp.stocktrack.subscriptions.domain.model.commands.CreateAccountCommand;

public interface AccountCommandService {
    Account handle(CreateAccountCommand command);
}
