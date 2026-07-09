package com.inventiapp.stocktrack.inventory.interfaces.rest.transform;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.ExpirationAlertResource;

/**
 * Assembler to convert an ExpirationAlert aggregate to an ExpirationAlertResource.
 */
public class ExpirationAlertResourceFromEntityAssembler {

    /**
     * Converts an ExpirationAlert entity into an ExpirationAlertResource.
     *
     * @param alert the ExpirationAlert aggregate
     * @return ExpirationAlertResource for API responses
     */
    public static ExpirationAlertResource toResource(ExpirationAlert alert) {
        if (alert == null) {
            throw new IllegalArgumentException("ExpirationAlert cannot be null");
        }

        return new ExpirationAlertResource(
                alert.getId(),
                alert.getBatchId(),
                alert.getProductId(),
                alert.getExpirationDate(),
                alert.getStatus().name(),
                alert.getTriggeredAt(),
                alert.getActionType() != null ? alert.getActionType().name() : null,
                alert.getResolvedAt()
        );
    }
}
