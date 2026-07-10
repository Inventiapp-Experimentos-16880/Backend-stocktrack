package com.inventiapp.stocktrack.localization.application.internal.queryservices;

import com.inventiapp.stocktrack.localization.application.outboundservices.GeoLocationProvider;
import com.inventiapp.stocktrack.localization.domain.model.queries.GetLocalizationByIpQuery;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.CountryLanguagePolicy;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.LocalizationResult;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.LocalizationSource;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.SupportedLanguage;
import com.inventiapp.stocktrack.localization.domain.services.LocalizationQueryService;
import org.springframework.stereotype.Service;

/**
 * LocalizationQueryService implementation.
 *
 * It resolves the user's country through the geolocation provider
 * and selects a supported language using the domain policy.
 *
 * @since 1.0
 */
@Service
public class LocalizationQueryServiceImpl
        implements LocalizationQueryService {

    private final GeoLocationProvider geoLocationProvider;

    /**
     * Creates the localization query service.
     *
     * @param geoLocationProvider geolocation outbound service
     */
    public LocalizationQueryServiceImpl(
            GeoLocationProvider geoLocationProvider
    ) {
        this.geoLocationProvider = geoLocationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizationResult handle(
            GetLocalizationByIpQuery query
    ) {
        return geoLocationProvider
                .findCountryCodeByIp(query.ipAddress())
                .map(this::createIpLocalizationResult)
                .orElseGet(this::createFallbackLocalizationResult);
    }

    /**
     * Creates a localization result using the detected country.
     *
     * @param countryCode detected ISO country code
     * @return localization result
     */
    private LocalizationResult createIpLocalizationResult(
            String countryCode
    ) {
        var recommendedLanguage =
                CountryLanguagePolicy.resolve(countryCode);

        return new LocalizationResult(
                countryCode,
                recommendedLanguage,
                LocalizationSource.IP
        );
    }

    /**
     * Creates the result used when the location cannot be detected.
     *
     * @return fallback localization result
     */
    private LocalizationResult createFallbackLocalizationResult() {
        return new LocalizationResult(
                null,
                SupportedLanguage.ENGLISH,
                LocalizationSource.FALLBACK
        );
    }
}