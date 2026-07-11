package com.inventiapp.stocktrack.inventory.domain.services;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;

import java.util.List;

/**
 * Domain service that encapsulates the "near expiration" criterion for batches.
 * <p>
 * Reusable by the dashboard (its own threshold) and by the daily expiration scan (7 days).
 * A batch is considered near expiration when its expiration date is strictly after today and
 * on or before {@code today + thresholdDays} (i.e. already-expired batches and today are excluded,
 * but the exact Nth day is included).
 */
public interface ExpirationDetectionService {

    /**
     * Default threshold (in days) used by the daily expiration scan.
     */
    int DEFAULT_THRESHOLD_DAYS = 7;

    /**
     * Filters the given batches down to those expiring within the threshold window.
     * The input order is preserved.
     *
     * @param batches       batches to evaluate
     * @param thresholdDays size of the look-ahead window in days
     * @return batches expiring within the window, in input order
     */
    List<Batch> findExpiringSoon(List<Batch> batches, int thresholdDays);
}
