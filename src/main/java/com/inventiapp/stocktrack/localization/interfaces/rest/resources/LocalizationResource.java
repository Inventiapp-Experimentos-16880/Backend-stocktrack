package com.inventiapp.stocktrack.localization.interfaces.rest.resources;

/**
 * Resource representing the localization recommendation.
 *
 * @param countryCode detected ISO country code
 * @param recommendedLanguage recommended frontend language
 * @param source source used to determine the recommendation
 * @since 1.0
 */
public record LocalizationResource(
        String countryCode,
        String recommendedLanguage,
        String source
) {
}