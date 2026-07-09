package com.inventiapp.stocktrack.inventory.application.internal.queryservices;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationDetectionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Implementation of ExpirationDetectionService.
 * <p>
 * A batch is near expiration when its expiration date is strictly after today and on or before
 * {@code today + thresholdDays} (the Nth day is inclusive). Already-expired batches and batches
 * expiring today are excluded: US17 is preventive and does not cover consummated waste.
 * The input order is preserved so downstream callers that apply a limit keep selecting the
 * same batches as before.
 */
@Service
public class ExpirationDetectionServiceImpl implements ExpirationDetectionService {

    @Override
    public List<Batch> findExpiringSoon(List<Batch> batches, int thresholdDays) {
        if (batches == null || batches.isEmpty()) {
            return List.of();
        }

        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(thresholdDays);

        return batches.stream()
                .filter(b -> b.getExpirationDate() != null)
                .filter(b -> {
                    LocalDate expDate = b.getExpirationDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    // Inclusive upper bound: today < expDate <= today + thresholdDays.
                    return expDate.isAfter(today) && !expDate.isAfter(windowEnd);
                })
                .toList();
    }
}
