package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.localization.application.internal.queryservices.LocalizationQueryServiceImpl;
import com.inventiapp.stocktrack.localization.application.outboundservices.GeoLocationProvider;
import com.inventiapp.stocktrack.localization.domain.model.queries.GetLocalizationByIpQuery;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.LocalizationSource;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.SupportedLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalizationQueryServiceImplTest {

    @Mock
    private GeoLocationProvider geoLocationProvider;

    @Test
    void shouldReturnSpanishWhenDetectedCountryIsPeru() {
        // Arrange
        var localizationQueryService =
                new LocalizationQueryServiceImpl(geoLocationProvider);

        var ipAddress = "190.42.1.1";
        var query = new GetLocalizationByIpQuery(ipAddress);

        when(geoLocationProvider.findCountryCodeByIp(ipAddress))
                .thenReturn(Optional.of("PE"));

        // Act
        var result = localizationQueryService.handle(query);

        // Assert
        assertEquals("PE", result.countryCode());
        assertEquals(
                SupportedLanguage.SPANISH,
                result.recommendedLanguage()
        );
        assertEquals(LocalizationSource.IP, result.source());

        verify(
                geoLocationProvider,
                times(1)
        ).findCountryCodeByIp(ipAddress);
    }

    @Test
    void shouldReturnGermanWhenDetectedCountryIsGermany() {
        // Arrange
        var localizationQueryService =
                new LocalizationQueryServiceImpl(geoLocationProvider);

        var ipAddress = "80.1.1.1";
        var query = new GetLocalizationByIpQuery(ipAddress);

        when(geoLocationProvider.findCountryCodeByIp(ipAddress))
                .thenReturn(Optional.of("DE"));

        // Act
        var result = localizationQueryService.handle(query);

        // Assert
        assertEquals("DE", result.countryCode());
        assertEquals(
                SupportedLanguage.GERMAN,
                result.recommendedLanguage()
        );
        assertEquals(LocalizationSource.IP, result.source());

        verify(
                geoLocationProvider,
                times(1)
        ).findCountryCodeByIp(ipAddress);
    }

    @Test
    void shouldReturnFallbackWhenCountryCannotBeDetected() {
        // Arrange
        var localizationQueryService =
                new LocalizationQueryServiceImpl(geoLocationProvider);

        var ipAddress = "127.0.0.1";
        var query = new GetLocalizationByIpQuery(ipAddress);

        when(geoLocationProvider.findCountryCodeByIp(ipAddress))
                .thenReturn(Optional.empty());

        // Act
        var result = localizationQueryService.handle(query);

        // Assert
        assertNull(result.countryCode());
        assertEquals(
                SupportedLanguage.ENGLISH,
                result.recommendedLanguage()
        );
        assertEquals(
                LocalizationSource.FALLBACK,
                result.source()
        );

        verify(
                geoLocationProvider,
                times(1)
        ).findCountryCodeByIp(ipAddress);
    }
}