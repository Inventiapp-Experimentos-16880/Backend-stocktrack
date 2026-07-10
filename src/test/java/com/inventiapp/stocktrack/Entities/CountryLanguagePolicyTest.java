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
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.SPANISH, result);
    }

    @Test
    void shouldReturnGermanForGermany() {
        // Arrange
        var countryCode = "DE";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.GERMAN, result);
    }

    @Test
    void shouldReturnEnglishForUnitedStates() {
        // Arrange
        var countryCode = "US";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }

    @Test
    void shouldReturnFrenchForFrance() {
        // Arrange
        var countryCode = "FR";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.FRENCH, result);
    }

    @Test
    void shouldReturnPortugueseForBrazil() {
        // Arrange
        var countryCode = "BR";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.PORTUGUESE, result);
    }

    @Test
    void shouldReturnItalianForItaly() {
        // Arrange
        var countryCode = "IT";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.ITALIAN, result);
    }

    @Test
    void shouldReturnJapaneseForJapan() {
        // Arrange
        var countryCode = "JP";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.JAPANESE, result);
    }

    @Test
    void shouldNormalizeLowercaseSpanishCountryCode() {
        // Arrange
        var countryCode = "pe";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.SPANISH, result);
    }

    @Test
    void shouldReturnPortugueseForLowercaseBrazilCountryCode() {
        // Arrange
        var countryCode = "br";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.PORTUGUESE, result);
    }

    @Test
    void shouldReturnEnglishWhenCountryCodeIsNull() {
        // Act
        var result = CountryLanguagePolicy.recommendFor(null);

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }

    @Test
    void shouldReturnEnglishWhenCountryCodeIsBlank() {
        // Act
        var result = CountryLanguagePolicy.recommendFor(" ");

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }

    @Test
    void shouldReturnEnglishForUnsupportedCountry() {
        // Arrange
        var countryCode = "KR";

        // Act
        var result = CountryLanguagePolicy.recommendFor(countryCode);

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }
}