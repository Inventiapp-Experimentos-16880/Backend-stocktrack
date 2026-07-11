package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.inventory.application.internal.commandservices.ExpirationAlertCommandServiceImpl;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.AlertStatus;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ExpirationAlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Covers the idempotency of raising near-expiration alerts (US17 part 1):
 * a second scan must not create a duplicate PENDING alert for the same batch.
 */
@ExtendWith(MockitoExtension.class)
class ExpirationAlertCommandServiceTest {

    @Mock
    private ExpirationAlertRepository expirationAlertRepository;

    @InjectMocks
    private ExpirationAlertCommandServiceImpl service;

    private RaiseExpirationAlertCommand command(long batchId, long ownerId) {
        return new RaiseExpirationAlertCommand(batchId, 10L, new Date(), new Date(), ownerId);
    }

    @Test
    void createsAlertWhenNoPendingAlertExists() {
        long batchId = 30L;
        long ownerId = 5L;
        when(expirationAlertRepository.existsByBatchIdAndOwnerIdAndStatus(batchId, ownerId, AlertStatus.PENDING))
                .thenReturn(false);

        ExpirationAlert saved = mock(ExpirationAlert.class);
        when(saved.getId()).thenReturn(42L);
        when(expirationAlertRepository.save(any(ExpirationAlert.class))).thenReturn(saved);

        Optional<Long> result = service.handle(command(batchId, ownerId));

        assertTrue(result.isPresent());
        assertEquals(42L, result.get());
        verify(expirationAlertRepository, times(1)).save(any(ExpirationAlert.class));
    }

    @Test
    void doesNotDuplicateWhenPendingAlertAlreadyExists() {
        long batchId = 30L;
        long ownerId = 5L;
        when(expirationAlertRepository.existsByBatchIdAndOwnerIdAndStatus(batchId, ownerId, AlertStatus.PENDING))
                .thenReturn(true);

        Optional<Long> result = service.handle(command(batchId, ownerId));

        assertTrue(result.isEmpty());
        verify(expirationAlertRepository, never()).save(any(ExpirationAlert.class));
    }
}
