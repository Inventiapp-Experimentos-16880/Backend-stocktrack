package com.inventiapp.stocktrack.inventory.domain.services;

import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;

import java.util.Optional;

/**
 * Command service for the ExpirationAlert aggregate.
 * Raises near-expiration alerts in an idempotent way.
 */
public interface ExpirationAlertCommandService {

    /**
     * Handle raising an expiration alert for a batch.
     * <p>
     * Idempotent: if a PENDING alert already exists for the batch/owner, no new alert is
     * created and an empty Optional is returned.
     *
     * @param command the raise expiration alert command
     * @return the generated alert id if a new alert was created, empty if one already existed
     */
    Optional<Long> handle(RaiseExpirationAlertCommand command);
}
