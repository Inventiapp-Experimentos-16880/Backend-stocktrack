package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.localization.domain.model.valueobjects.CountryLanguagePolicy;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.SupportedLanguage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountryLanguagePolicyTest {

    @Test
    void shouldReturnSpanishForPeru() {
        // Arrange
        var countryCode = "PE";

        // Act
        var result = CountryLanguagePolicy.resolve(countryCode);

        // Assert
        assertEquals(SupportedLanguage.SPANISH, result);
    }

    @Test
    void shouldReturnGermanForGermany() {
        // Arrange
        var countryCode = "DE";

        // Act
        var result = CountryLanguagePolicy.resolve(countryCode);

        // Assert
        assertEquals(SupportedLanguage.GERMAN, result);
    }

    @Test
    void shouldReturnEnglishForUnitedStates() {
        // Arrange
        var countryCode = "US";

        // Act
        var result = CountryLanguagePolicy.resolve(countryCode);

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }

    @Test
    void shouldNormalizeLowercaseCountryCode() {
        // Arrange
        var countryCode = "pe";

        // Act
        var result = CountryLanguagePolicy.resolve(countryCode);

        // Assert
        assertEquals(SupportedLanguage.SPANISH, result);
    }

    @Test
    void shouldReturnEnglishWhenCountryCodeIsNull() {
        // Act
        var result = CountryLanguagePolicy.resolve(null);

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }

    @Test
    void shouldReturnEnglishWhenCountryCodeIsBlank() {
        // Act
        var result = CountryLanguagePolicy.resolve(" ");

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }
}