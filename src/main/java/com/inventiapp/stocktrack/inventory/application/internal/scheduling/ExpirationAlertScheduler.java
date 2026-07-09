package com.inventiapp.stocktrack.inventory.application.internal.scheduling;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RaiseExpirationAlertCommand;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationDetectionService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.BatchRepository;
import com.inventiapp.stocktrack.shared.infrastructure.telemetry.TelemetryEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Daily scan that raises near-expiration alerts for batches expiring within the configured
 * threshold (default 7 days).
 * <p>
 * The job runs outside any HTTP request, so the owner is taken from each batch (there is no
 * authenticated user context). Alert creation is idempotent, so re-running the scan never
 * produces duplicate PENDING alerts. Alerts are only resolved by an explicit mitigation action
 * (US17 part 2); this job never closes or expires them.
 */
@Component
public class ExpirationAlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpirationAlertScheduler.class);

    private final BatchRepository batchRepository;
    private final ExpirationDetectionService expirationDetectionService;
    private final ExpirationAlertCommandService expirationAlertCommandService;
    private final TelemetryEventPublisher telemetryEventPublisher;

    private final int thresholdDays;

    public ExpirationAlertScheduler(
            BatchRepository batchRepository,
            ExpirationDetectionService expirationDetectionService,
            ExpirationAlertCommandService expirationAlertCommandService,
            TelemetryEventPublisher telemetryEventPublisher,
            @Value("${stocktrack.expiration-alert.threshold-days:" + ExpirationDetectionService.DEFAULT_THRESHOLD_DAYS + "}")
            int thresholdDays) {
        this.batchRepository = batchRepository;
        this.expirationDetectionService = expirationDetectionService;
        this.expirationAlertCommandService = expirationAlertCommandService;
        this.telemetryEventPublisher = telemetryEventPublisher;
        this.thresholdDays = thresholdDays;
    }

    /**
     * Cron-triggered entry point. Defaults to a daily run at 02:00 server time.
     */
    @Scheduled(cron = "${stocktrack.scheduling.expiration-scan.cron:0 0 2 * * *}")
    public void runDailyScan() {
        int created = scan();
        log.info("Expiration scan finished: {} new alert(s) created", created);
    }

    /**
     * Scans every batch, raises a PENDING alert for each one expiring within the threshold, and
     * publishes a telemetry event for every newly created alert. Exposed (package-visible) so it
     * can be driven directly from tests without the cron trigger.
     *
     * @return the number of new alerts created in this run
     */
    public int scan() {
        List<Batch> batches = batchRepository.findAll();
        List<Batch> expiringSoon = expirationDetectionService.findExpiringSoon(batches, thresholdDays);

        Date triggeredAt = new Date();
        int created = 0;

        for (Batch batch : expiringSoon) {
            var command = new RaiseExpirationAlertCommand(
                    batch.getId(),
                    batch.getProductId(),
                    batch.getExpirationDate(),
                    triggeredAt,
                    batch.getOwnerId()
            );

            var newAlertId = expirationAlertCommandService.handle(command);
            if (newAlertId.isPresent()) {
                created++;
                telemetryEventPublisher.publishBatchAlertTriggered(
                        batch.getOwnerId(),
                        newAlertId.get(),
                        batch.getId(),
                        batch.getProductId(),
                        batch.getExpirationDate()
                );
            }
        }

        return created;
    }
}
