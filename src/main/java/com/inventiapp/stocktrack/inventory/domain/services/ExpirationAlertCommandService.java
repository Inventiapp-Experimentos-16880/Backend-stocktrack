package com.inventiapp.stocktrack.inventory.domain.services;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RegisterMitigationActionCommand;

import java.util.Optional;

/**
 * Command service for the ExpirationAlert aggregate.
 * Raises near-expiration alerts in an idempotent way and resolves them via mitigation actions.
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

    /**
     * Handle registering a mitigation action against a PENDING alert (US17 part 2).
     * <p>
     * Resolves the alert (RESOLVED, with action type and timestamp) and drives the stock decrease
     * of the associated batch through the existing UpdateBatch flow (which records the SALIDA).
     *
     * @param command the register mitigation action command
     * @return the resolved alert
     */
    ExpirationAlert handle(RegisterMitigationActionCommand command);
}
