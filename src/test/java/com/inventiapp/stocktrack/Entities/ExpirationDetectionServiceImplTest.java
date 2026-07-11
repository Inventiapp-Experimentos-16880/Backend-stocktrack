package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.inventory.application.internal.queryservices.ExpirationDetectionServiceImpl;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationDetectionService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Covers the "near expiration" criterion: a batch inside the date window only counts when it still
 * holds stock, since US17 has nothing to mitigate on an empty batch.
 */
class ExpirationDetectionServiceImplTest {

    private final ExpirationDetectionService detectionService = new ExpirationDetectionServiceImpl();

    private static Date daysFromNow(int days) {
        return Date.from(LocalDate.now().plusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static Batch batch(long id, int daysToExpiration, Integer quantity) {
        Batch batch = mock(Batch.class);
        lenient().when(batch.getId()).thenReturn(id);
        lenient().when(batch.getQuantity()).thenReturn(quantity);
        lenient().when(batch.getExpirationDate()).thenReturn(daysFromNow(daysToExpiration));
        return batch;
    }

    @Test
    void excludesBatchesWithoutStockEvenWhenInsideTheDateWindow() {
        Batch withStock = batch(40L, 3, 4);
        Batch emptyBatch = batch(41L, 3, 0);
        Batch nullQuantity = batch(42L, 3, null);

        List<Batch> result = detectionService.findExpiringSoon(
                List.of(withStock, emptyBatch, nullQuantity),
                ExpirationDetectionService.DEFAULT_THRESHOLD_DAYS);

        assertEquals(List.of(withStock), result);
    }
}
