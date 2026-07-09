package com.inventiapp.stocktrack.inventory.application.internal.commandservices;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertCommandService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ExpirationAlertRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ExpirationAlertCommandService.
 * Raises near-expiration alerts, guaranteeing at most one PENDING alert per batch/owner.
 */
@Service
public class ExpirationAlertCommandServiceImpl implements ExpirationAlertCommandService {

    private final ExpirationAlertRepository expirationAlertRepository;

    public ExpirationAlertCommandServiceImpl(ExpirationAlertRepository expirationAlertRepository) {
        this.expirationAlertRepository = expirationAlertRepository;
    }

    @Override
    public Optional<Long> handle(RaiseExpirationAlertCommand command) {
        // Idempotency: never create a second PENDING alert for the same batch on successive scans.
        if (expirationAlertRepository.existsByBatchIdAndOwnerIdAndStatus(
                command.batchId(), command.ownerId(), AlertStatus.PENDING)) {
            return Optional.empty();
        }

        ExpirationAlert alert = new ExpirationAlert(command);
        try {
            ExpirationAlert saved = expirationAlertRepository.save(alert);
            return Optional.of(saved.getId());
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error saving expiration alert: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }
}
