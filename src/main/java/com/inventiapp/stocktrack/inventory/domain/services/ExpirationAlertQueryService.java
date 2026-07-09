package com.inventiapp.stocktrack.inventory.domain.services;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllExpirationAlertsQuery;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetExpirationAlertsByStatusQuery;

import java.util.List;

/**
 * Query service for the ExpirationAlert aggregate.
 * Provides read access to the alerts of an owner.
 */
public interface ExpirationAlertQueryService {

    /**
     * Handle query to get all expiration alerts of an owner.
     *
     * @param query get all expiration alerts query
     * @return list of alerts for the owner
     */
    List<ExpirationAlert> handle(GetAllExpirationAlertsQuery query);

    /**
     * Handle query to get expiration alerts of an owner filtered by status.
     *
     * @param query get expiration alerts by status query
     * @return list of alerts for the owner with the given status
     */
    List<ExpirationAlert> handle(GetExpirationAlertsByStatusQuery query);
}
