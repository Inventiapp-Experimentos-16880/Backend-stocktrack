package com.inventiapp.stocktrack.localization.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Languages supported by the StockTrack user interface.
 *
 * @since 1.0
 */
public enum SupportedLanguage {

    SPANISH("es"),
    ENGLISH("en"),
    GERMAN("de");

    private final String code;

    SupportedLanguage(String code) {
        this.code = code;
    }

    /**
     * Returns the ISO language code used by the frontend.
     *
     * @return language code
     */
    public String getCode() {
        return code;
    }

    /**
     * Finds a supported language by its code.
     *
     * @param code language code
     * @return supported language
     * @throws IllegalArgumentException when the language is unsupported
     */
    public static SupportedLanguage fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Language code cannot be null or blank"
            );
        }

        return Arrays.stream(values())
                .filter(language ->
                        language.code.equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unsupported language code: " + code
                        ));
    }
}