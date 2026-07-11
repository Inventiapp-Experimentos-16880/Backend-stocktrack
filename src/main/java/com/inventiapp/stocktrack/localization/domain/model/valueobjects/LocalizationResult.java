package com.inventiapp.stocktrack.localization.domain.model.valueobjects;

/**
 * Result of the localization detection process.
 *
 * @param countryCode detected ISO country code
 * @param recommendedLanguage recommended interface language
 * @param source source used for the recommendation
 * @since 1.0
 */
public record LocalizationResult(
        String countryCode,
        SupportedLanguage recommendedLanguage,
        LocalizationSource source
) {

    public LocalizationResult {
        if (recommendedLanguage == null) {
            throw new IllegalArgumentException(
                    "Recommended language is required"
            );
        }

        if (source == null) {
            throw new IllegalArgumentException(
                    "Localization source is required"
            );
        }

        if (countryCode != null) {
            countryCode = countryCode.trim().toUpperCase();
        }
    }
}