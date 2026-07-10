package com.inventiapp.stocktrack.localization.domain.services;

import com.inventiapp.stocktrack.localization.domain.model.queries.GetLocalizationByIpQuery;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.LocalizationResult;

/**
 * Domain service for localization queries.
 *
 * @since 1.0
 */
public interface LocalizationQueryService {

    /**
     * Obtains a language recommendation for an IP address.
     *
     * @param query localization query
     * @return localization result
     */
    LocalizationResult handle(GetLocalizationByIpQuery query);
}