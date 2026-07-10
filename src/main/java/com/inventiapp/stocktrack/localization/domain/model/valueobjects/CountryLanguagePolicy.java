package com.inventiapp.stocktrack.localization.domain.model.valueobjects;

import java.util.Locale;
import java.util.Set;

/**
 * Domain policy responsible for mapping a country code
 * to one of the languages supported by the application.
 */
public final class CountryLanguagePolicy {

    private static final Set<String> SPANISH_COUNTRY_CODES = Set.of(
            "AR",
            "BO",
            "CL",
            "CO",
            "CR",
            "CU",
            "DO",
            "EC",
            "SV",
            "GQ",
            "GT",
            "HN",
            "MX",
            "NI",
            "PA",
            "PY",
            "PE",
            "PR",
            "ES",
            "UY",
            "VE"
    );

    private static final Set<String> GERMAN_COUNTRY_CODES = Set.of(
            "DE",
            "AT",
            "LI"
    );

    private static final Set<String> FRENCH_COUNTRY_CODES = Set.of(
            "FR",
            "MC"
    );

    private static final Set<String> PORTUGUESE_COUNTRY_CODES = Set.of(
            "PT",
            "BR",
            "AO",
            "MZ",
            "CV",
            "GW",
            "ST",
            "TL"
    );

    private static final Set<String> ITALIAN_COUNTRY_CODES = Set.of(
            "IT",
            "SM",
            "VA"
    );

    private static final Set<String> JAPANESE_COUNTRY_CODES = Set.of(
            "JP"
    );

    private CountryLanguagePolicy() {
    }

    /**
     * Returns the language recommended for a country.
     *
     * Countries without an explicitly supported language
     * use English as the default language.
     *
     * @param countryCode ISO 3166-1 alpha-2 country code
     * @return recommended supported language
     */
    public static SupportedLanguage recommendFor(
            String countryCode
    ) {
        if (countryCode == null || countryCode.isBlank()) {
            return SupportedLanguage.ENGLISH;
        }

        var normalizedCountryCode = countryCode
                .trim()
                .toUpperCase(Locale.ROOT);

        if (SPANISH_COUNTRY_CODES.contains(normalizedCountryCode)) {
            return SupportedLanguage.SPANISH;
        }

        if (GERMAN_COUNTRY_CODES.contains(normalizedCountryCode)) {
            return SupportedLanguage.GERMAN;
        }

        if (FRENCH_COUNTRY_CODES.contains(normalizedCountryCode)) {
            return SupportedLanguage.FRENCH;
        }

        if (PORTUGUESE_COUNTRY_CODES.contains(normalizedCountryCode)) {
            return SupportedLanguage.PORTUGUESE;
        }

        if (ITALIAN_COUNTRY_CODES.contains(normalizedCountryCode)) {
            return SupportedLanguage.ITALIAN;
        }

        if (JAPANESE_COUNTRY_CODES.contains(normalizedCountryCode)) {
            return SupportedLanguage.JAPANESE;
        }

        return SupportedLanguage.ENGLISH;
    }
}