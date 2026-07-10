package com.inventiapp.stocktrack.localization.domain.model.valueobjects;

import java.util.Locale;
import java.util.Set;

/**
 * Domain policy responsible for resolving the recommended language
 * according to an ISO country code.
 *
 * @since 1.0
 */
public final class CountryLanguagePolicy {

    private static final Set<String> SPANISH_COUNTRY_CODES = Set.of(
            "PE",
            "ES",
            "MX",
            "AR",
            "BO",
            "CL",
            "CO",
            "CR",
            "CU",
            "DO",
            "EC",
            "SV",
            "GT",
            "HN",
            "NI",
            "PA",
            "PY",
            "PR",
            "UY",
            "VE",
            "GQ"
    );

    private static final Set<String> GERMAN_COUNTRY_CODES = Set.of(
            "DE",
            "AT",
            "LI"
    );

    private CountryLanguagePolicy() {
        /*
         * Prevents class instantiation because this class only
         * provides domain policy methods.
         */
    }

    /**
     * Resolves the recommended language for a country.
     *
     * @param countryCode ISO country code
     * @return recommended supported language
     */
    public static SupportedLanguage resolve(String countryCode) {
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

        return SupportedLanguage.ENGLISH;
    }
}