package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.inventory.application.internal.queryservices.ExpirationDetectionServiceImpl;
import com.inventiapp.stocktrack.inventory.application.internal.scheduling.ExpirationAlertScheduler;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationDetectionService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.BatchRepository;
import com.inventiapp.stocktrack.shared.infrastructure.telemetry.TelemetryEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Covers the US17 acceptance criteria for the daily expiration scan:
 * a batch expiring within 7 days yields a PENDING alert (with telemetry). The threshold day is now
 * inclusive, so a batch expiring exactly on day 7 alerts while day 8 (and already-expired batches)
 * do not. Idempotency at scan level: when the alert already exists no telemetry is published.
 */
@ExtendWith(MockitoExtension.class)
class ExpirationAlertSchedulerTest {

    private static final long OWNER_ID = 5L;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private ExpirationAlertCommandService expirationAlertCommandService;

    @Mock
    private TelemetryEventPublisher telemetryEventPublisher;

    // Real detection service so the 7-day boundary is exercised end to end.
    private final ExpirationDetectionService detectionService = new ExpirationDetectionServiceImpl();

    private ExpirationAlertScheduler scheduler;

    @BeforeEach
    void setup() {
        scheduler = new ExpirationAlertScheduler(
                batchRepository,
                detectionService,
                expirationAlertCommandService,
                telemetryEventPublisher,
                ExpirationDetectionService.DEFAULT_THRESHOLD_DAYS // 7
        );
    }

    private static Date daysFromNow(int days) {
        return Date.from(LocalDate.now().plusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Batch batch(long id, int daysToExpiration) {
        Batch batch = mock(Batch.class);
        // Lenient: batches filtered out by the detection service never read id/product/owner.
        lenient().when(batch.getId()).thenReturn(id);
        lenient().when(batch.getProductId()).thenReturn(10L);
        lenient().when(batch.getOwnerId()).thenReturn(OWNER_ID);
        // The detection service only looks at batches that still hold stock.
        when(batch.getQuantity()).thenReturn(4);
        when(batch.getExpirationDate()).thenReturn(daysFromNow(daysToExpiration));
        return batch;
    }

    @Test
    void raisesAlertAndPublishesTelemetryForBatchesExpiringWithinSevenDaysInclusive() {
        Batch expiringSoon = batch(30L, 3);    // within window -> alert
        Batch onSeventhDay = batch(31L, 7);    // inclusive upper bound -> alert
        Batch onEighthDay = batch(32L, 8);     // just beyond window -> no alert
        Batch alreadyExpired = batch(33L, -1); // already expired -> no alert

        when(batchRepository.findAll())
                .thenReturn(List.of(expiringSoon, onSeventhDay, onEighthDay, alreadyExpired));
        when(expirationAlertCommandService.handle(any(RaiseExpirationAlertCommand.class)))
                .thenReturn(Optional.of(100L));

        int created = scheduler.scan();

        assertEquals(2, created);

        ArgumentCaptor<RaiseExpirationAlertCommand> captor =
                ArgumentCaptor.forClass(RaiseExpirationAlertCommand.class);
        verify(expirationAlertCommandService, times(2)).handle(captor.capture());
        assertEquals(List.of(30L, 31L),
                captor.getAllValues().stream().map(RaiseExpirationAlertCommand::batchId).toList());

        verify(telemetryEventPublisher, times(2))
                .publishBatchAlertTriggered(eq(OWNER_ID), eq(100L), anyLong(), eq(10L), any(Date.class));
    }

    @Test
    void doesNotPublishTelemetryWhenAlertAlreadyExists() {
        Batch expiringSoon = batch(30L, 3);

        when(batchRepository.findAll()).thenReturn(List.of(expiringSoon));
        // Idempotent command service reports "nothing created" on a repeat scan.
        when(expirationAlertCommandService.handle(any(RaiseExpirationAlertCommand.class)))
                .thenReturn(Optional.empty());

        int created = scheduler.scan();

        assertEquals(0, created);
        verify(expirationAlertCommandService, times(1)).handle(any(RaiseExpirationAlertCommand.class));
        verify(telemetryEventPublisher, never())
                .publishBatchAlertTriggered(anyLong(), anyLong(), anyLong(), anyLong(), any(Date.class));
    }
}
