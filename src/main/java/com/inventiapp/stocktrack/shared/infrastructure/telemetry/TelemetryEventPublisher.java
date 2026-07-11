package com.inventiapp.stocktrack.shared.infrastructure.telemetry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventiapp.stocktrack.shared.infrastructure.telemetry.model.ExperimentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Publishes generic telemetry events (tracking plan 8.2.8) into the experiment_events store.
 * <p>
 * Kept decoupled from the domain: it is invoked from application services, never from within
 * aggregates. Publishing is best-effort — a telemetry failure never propagates to the caller.
 */
@Service
public class TelemetryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TelemetryEventPublisher.class);

    static final String BATCH_ALERT_TRIGGERED = "batch_alert_triggered";
    static final String BATCH_ALERT_ACTION = "batch_alert_action";

    private final ExperimentEventRepository experimentEventRepository;
    private final ObjectMapper objectMapper;

    public TelemetryEventPublisher(ExperimentEventRepository experimentEventRepository,
                                   ObjectMapper objectMapper) {
        this.experimentEventRepository = experimentEventRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Records a "batch_alert_triggered" event for a newly created expiration alert.
     *
     * @param ownerId        owner the alert belongs to
     * @param alertId        id of the created alert (used as entityId)
     * @param batchId        batch the alert refers to
     * @param productId      product the batch belongs to
     * @param expirationDate expiration date of the batch
     */
    public void publishBatchAlertTriggered(Long ownerId, Long alertId, Long batchId,
                                           Long productId, Date expirationDate) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("batchId", batchId);
            payload.put("productId", productId);
            payload.put("expirationDate", expirationDate);

            ExperimentEvent event = new ExperimentEvent(
                    ownerId,
                    BATCH_ALERT_TRIGGERED,
                    alertId,
                    objectMapper.writeValueAsString(payload),
                    new Date()
            );
            experimentEventRepository.save(event);
        } catch (Exception ex) {
            // Telemetry is best-effort: never break the business flow because tracking failed.
            log.warn("Failed to publish {} event for alert {}: {}",
                    BATCH_ALERT_TRIGGERED, alertId, ex.getMessage());
        }
    }

    /**
     * Records a "batch_alert_action" event when a mitigation action resolves an expiration alert.
     * Enables the batch_alert_triggered -> batch_alert_action funnel for the Alert Action Rate (8.2.3).
     *
     * @param ownerId    owner the alert belongs to
     * @param alertId    id of the resolved alert (used as entityId)
     * @param batchId    batch the alert refers to
     * @param actionType mitigation action taken (e.g. LIQUIDATION/RETURN)
     * @param quantity   units liquidated/returned
     * @param occurredAt timestamp when the action was registered
     */
    public void publishBatchAlertAction(Long ownerId, Long alertId, Long batchId,
                                        String actionType, Integer quantity, Date occurredAt) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("batchId", batchId);
            payload.put("actionType", actionType);
            payload.put("quantity", quantity);
            payload.put("occurredAt", occurredAt);

            ExperimentEvent event = new ExperimentEvent(
                    ownerId,
                    BATCH_ALERT_ACTION,
                    alertId,
                    objectMapper.writeValueAsString(payload),
                    occurredAt
            );
            experimentEventRepository.save(event);
        } catch (Exception ex) {
            // Telemetry is best-effort: never break the business flow because tracking failed.
            log.warn("Failed to publish {} event for alert {}: {}",
                    BATCH_ALERT_ACTION, alertId, ex.getMessage());
        }
    }
}
