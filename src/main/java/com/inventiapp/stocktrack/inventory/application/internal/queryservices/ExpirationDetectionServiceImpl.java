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
 * Replicates the pre-existing dashboard criterion: a batch is near expiration when its
 * expiration date is strictly after today and strictly before {@code today + thresholdDays}.
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
                    return expDate.isBefore(windowEnd) && expDate.isAfter(today);
                })
                .toList();
    }
}
