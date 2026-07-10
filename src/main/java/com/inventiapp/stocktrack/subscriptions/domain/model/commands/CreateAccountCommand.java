package com.inventiapp.stocktrack.subscriptions.domain.model.commands;

public record CreateAccountCommand(
        Long ownerId,
        String email
) {
}
