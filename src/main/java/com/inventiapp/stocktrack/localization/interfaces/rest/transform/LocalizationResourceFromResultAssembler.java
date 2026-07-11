package com.inventiapp.stocktrack.localization.interfaces.rest.transform;

import com.inventiapp.stocktrack.localization.domain.model.valueobjects.LocalizationResult;
import com.inventiapp.stocktrack.localization.interfaces.rest.resources.LocalizationResource;

/**
 * Assembler to create a LocalizationResource from a LocalizationResult.
 *
 * @since 1.0
 */
public class LocalizationResourceFromResultAssembler {

    /**
     * Converts a LocalizationResult into a LocalizationResource.
     *
     * @param result localization domain result
     * @return localization REST resource
     */
    public static LocalizationResource toResourceFromResult(
            LocalizationResult result
    ) {
        return new LocalizationResource(
                result.countryCode(),
                result.recommendedLanguage().getCode(),
                result.source().name()
        );
    }
}