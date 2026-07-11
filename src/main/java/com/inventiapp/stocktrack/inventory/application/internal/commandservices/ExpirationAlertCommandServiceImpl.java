package com.inventiapp.stocktrack.inventory.application.internal.commandservices;

import com.inventiapp.stocktrack.inventory.domain.exceptions.AlertAlreadyResolvedException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.BatchNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ExpirationAlertNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RegisterMitigationActionCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.UpdateBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;
import com.inventiapp.stocktrack.inventory.domain.services.BatchCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertCommandService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.BatchRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ExpirationAlertRepository;
import com.inventiapp.stocktrack.shared.infrastructure.telemetry.TelemetryEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ExpirationAlertCommandService.
 * Raises near-expiration alerts (at most one PENDING per batch/owner) and resolves them by
 * registering a mitigation action, delegating the stock decrease to the existing UpdateBatch flow.
 */
@Service
public class ExpirationAlertCommandServiceImpl implements ExpirationAlertCommandService {

    private final ExpirationAlertRepository expirationAlertRepository;
    private final BatchRepository batchRepository;
    private final BatchCommandService batchCommandService;
    private final TelemetryEventPublisher telemetryEventPublisher;

    public ExpirationAlertCommandServiceImpl(ExpirationAlertRepository expirationAlertRepository,
                                             BatchRepository batchRepository,
                                             BatchCommandService batchCommandService,
                                             TelemetryEventPublisher telemetryEventPublisher) {
        this.expirationAlertRepository = expirationAlertRepository;
        this.batchRepository = batchRepository;
        this.batchCommandService = batchCommandService;
        this.telemetryEventPublisher = telemetryEventPublisher;
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

    @Override
    public ExpirationAlert handle(RegisterMitigationActionCommand command) {
        // Load the alert scoped to the owner.
        ExpirationAlert alert = expirationAlertRepository
                .findByIdAndOwnerId(command.alertId(), command.ownerId())
                .orElseThrow(() -> new ExpirationAlertNotFoundException(command.alertId()));

        // Guard before touching stock: a non-PENDING alert must not alter inventory.
        // (resolve() re-validates this; the explicit check keeps the flow side-effect free on 409.)
        if (alert.getStatus() != AlertStatus.PENDING) {
            throw new AlertAlreadyResolvedException(alert.getId());
        }

        Batch batch = batchRepository.findByIdAndOwnerId(alert.getBatchId(), command.ownerId())
                .orElseThrow(() -> new BatchNotFoundException(alert.getBatchId()));

        // Quantity of the action: caller-provided amount, or the full remaining batch by default.
        int available = batch.getQuantity();
        int actionQuantity = command.quantity() != null ? command.quantity() : available;
        if (actionQuantity <= 0 || actionQuantity > available) {
            throw new IllegalArgumentException(
                    "Action quantity must be between 1 and the batch's available quantity (" + available + ")");
        }

        // Delegate the stock decrease to the existing UpdateBatch flow, which records the SALIDA
        // in stock_movements (US18). No StockMovement is registered here to avoid duplication.
        int newQuantity = available - actionQuantity;
        batchCommandService.handle(new UpdateBatchCommand(batch.getId(), newQuantity, command.ownerId()));

        // Resolve the alert (validates PENDING, sets actionType + actionQuantity + resolvedAt + RESOLVED).
        // actionQuantity is the effective amount that left the stock, not the raw request value:
        // when the caller omits it, the full batch quantity computed above is recorded.
        alert.resolve(command.actionType(), actionQuantity);
        ExpirationAlert resolved = expirationAlertRepository.save(alert);

        // Best-effort telemetry: enables the batch_alert_triggered -> batch_alert_action funnel.
        telemetryEventPublisher.publishBatchAlertAction(
                resolved.getOwnerId(),
                resolved.getId(),
                resolved.getBatchId(),
                resolved.getActionType().name(),
                actionQuantity,
                resolved.getResolvedAt()
        );

        return resolved;
    }
}
