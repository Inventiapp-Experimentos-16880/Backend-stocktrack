package com.inventiapp.stocktrack.inventory.interfaces.rest.resources;

import java.util.Date;

/**
 * Resource record for an expiration alert.
 *
 * @param id             alert id
 * @param batchId        batch the alert applies to
 * @param productId      product the batch belongs to
 * @param expirationDate expiration date of the batch
 * @param status         alert status (PENDING/RESOLVED)
 * @param triggeredAt    timestamp when the alert was raised
 */
public record ExpirationAlertResource(
        Long id,
        Long batchId,
        Long productId,
        Date expirationDate,
        String status,
        Date triggeredAt
) {}
