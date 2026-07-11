package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.inventory.application.internal.commandservices.ExpirationAlertCommandServiceImpl;
import com.inventiapp.stocktrack.inventory.domain.exceptions.AlertAlreadyResolvedException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RegisterMitigationActionCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.UpdateBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MitigationActionType;
import com.inventiapp.stocktrack.inventory.domain.services.BatchCommandService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.BatchRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ExpirationAlertRepository;
import com.inventiapp.stocktrack.shared.infrastructure.telemetry.TelemetryEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Covers US17 part 2: registering a mitigation action against a near-expiration alert.
 * A PENDING alert becomes RESOLVED (with action type + timestamp) and the stock decrease is
 * delegated to the existing UpdateBatch flow (which records the SALIDA in stock_movements, US18);
 * a non-PENDING alert or an out-of-range quantity is rejected without touching stock.
 */
@ExtendWith(MockitoExtension.class)
class ExpirationAlertMitigationServiceTest {

    private static final long OWNER_ID = 5L;
    private static final long BATCH_ID = 50L;

    @Mock
    private ExpirationAlertRepository expirationAlertRepository;
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private BatchCommandService batchCommandService;
    @Mock
    private TelemetryEventPublisher telemetryEventPublisher;

    private ExpirationAlertCommandServiceImpl service;

    @BeforeEach
    void setup() {
        service = new ExpirationAlertCommandServiceImpl(
                expirationAlertRepository, batchRepository, batchCommandService, telemetryEventPublisher);
    }

    private ExpirationAlert pendingAlert() {
        return new ExpirationAlert(new RaiseExpirationAlertCommand(
                BATCH_ID, 10L, new Date(), new Date(), OWNER_ID));
    }

    private Batch batchWithQuantity(int quantity) {
        Batch batch = mock(Batch.class);
        lenient().when(batch.getId()).thenReturn(BATCH_ID);
        lenient().when(batch.getQuantity()).thenReturn(quantity);
        return batch;
    }

    @Test
    void resolvesPendingAlertAndDelegatesStockExitToUpdateBatch() {
        ExpirationAlert alert = pendingAlert();
        Batch batch = batchWithQuantity(20);
        when(expirationAlertRepository.findByIdAndOwnerId(1L, OWNER_ID)).thenReturn(Optional.of(alert));
        when(batchRepository.findByIdAndOwnerId(BATCH_ID, OWNER_ID)).thenReturn(Optional.of(batch));
        when(expirationAlertRepository.save(any(ExpirationAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        // No quantity supplied -> defaults to the full remaining batch quantity (20).
        var command = new RegisterMitigationActionCommand(1L, MitigationActionType.LIQUIDATION, null, OWNER_ID);
        ExpirationAlert result = service.handle(command);

        // Alert is now resolved with the action recorded. The omitted quantity means the whole
        // batch went out, so the full computed quantity (20) is what gets persisted.
        assertEquals(AlertStatus.RESOLVED, result.getStatus());
        assertEquals(MitigationActionType.LIQUIDATION, result.getActionType());
        assertEquals(20, result.getActionQuantity());
        assertNotNull(result.getResolvedAt());

        // Stock exit delegated to the existing UpdateBatch flow: newQuantity = 20 - 20 = 0,
        // a decrease that US18 records as a SALIDA. No StockMovement is registered here.
        ArgumentCaptor<UpdateBatchCommand> captor = ArgumentCaptor.forClass(UpdateBatchCommand.class);
        verify(batchCommandService, times(1)).handle(captor.capture());
        assertEquals(BATCH_ID, captor.getValue().batchId());
        assertEquals(0, captor.getValue().newQuantity());
        assertEquals(OWNER_ID, captor.getValue().ownerId());

        verify(telemetryEventPublisher, times(1))
                .publishBatchAlertAction(eq(OWNER_ID), any(), eq(BATCH_ID), eq("LIQUIDATION"), eq(20), any(Date.class));
    }

    @Test
    void persistsTheQuantityThatEffectivelyLeftTheStock() {
        ExpirationAlert alert = pendingAlert();
        Batch batch = batchWithQuantity(20);
        when(expirationAlertRepository.findByIdAndOwnerId(1L, OWNER_ID)).thenReturn(Optional.of(alert));
        when(batchRepository.findByIdAndOwnerId(BATCH_ID, OWNER_ID)).thenReturn(Optional.of(batch));
        when(expirationAlertRepository.save(any(ExpirationAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        // Partial liquidation: 7 of the 20 available units.
        var command = new RegisterMitigationActionCommand(1L, MitigationActionType.LIQUIDATION, 7, OWNER_ID);
        ExpirationAlert result = service.handle(command);

        // The alert records the same amount that was handed to UpdateBatch (20 - 7 = 13 left).
        assertEquals(7, result.getActionQuantity());
        ArgumentCaptor<UpdateBatchCommand> captor = ArgumentCaptor.forClass(UpdateBatchCommand.class);
        verify(batchCommandService).handle(captor.capture());
        assertEquals(13, captor.getValue().newQuantity());
    }

    @Test
    void pendingAlertHasNoActionQuantity() {
        assertNull(pendingAlert().getActionQuantity());
    }

    @Test
    void rejectsActionOnAlreadyResolvedAlertWithoutTouchingStock() {
        ExpirationAlert alert = pendingAlert();
        alert.resolve(MitigationActionType.RETURN, 3); // already RESOLVED
        when(expirationAlertRepository.findByIdAndOwnerId(1L, OWNER_ID)).thenReturn(Optional.of(alert));

        var command = new RegisterMitigationActionCommand(1L, MitigationActionType.LIQUIDATION, null, OWNER_ID);

        assertThrows(AlertAlreadyResolvedException.class, () -> service.handle(command));

        verify(batchCommandService, never()).handle(any(UpdateBatchCommand.class));
        verify(expirationAlertRepository, never()).save(any(ExpirationAlert.class));
        verify(telemetryEventPublisher, never())
                .publishBatchAlertAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void rejectsQuantityExceedingAvailableStockWithoutTouchingStock() {
        ExpirationAlert alert = pendingAlert();
        Batch batch = batchWithQuantity(20);
        when(expirationAlertRepository.findByIdAndOwnerId(1L, OWNER_ID)).thenReturn(Optional.of(alert));
        when(batchRepository.findByIdAndOwnerId(BATCH_ID, OWNER_ID)).thenReturn(Optional.of(batch));

        // Requested 999 exceeds the batch's available 20.
        var command = new RegisterMitigationActionCommand(1L, MitigationActionType.LIQUIDATION, 999, OWNER_ID);

        assertThrows(IllegalArgumentException.class, () -> service.handle(command));

        verify(batchCommandService, never()).handle(any(UpdateBatchCommand.class));
        verify(expirationAlertRepository, never()).save(any(ExpirationAlert.class));
    }
}
