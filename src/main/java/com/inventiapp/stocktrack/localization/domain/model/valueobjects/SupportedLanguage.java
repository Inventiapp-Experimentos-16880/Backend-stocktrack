package com.inventiapp.stocktrack.localization.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Languages supported by the application.
 */
public enum SupportedLanguage {

    SPANISH("es"),
    ENGLISH("en"),
    GERMAN("de"),
    FRENCH("fr"),
    PORTUGUESE("pt"),
    ITALIAN("it"),
    JAPANESE("ja");

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
     * Finds a supported language using its language code.
     *
     * @param code language code
     * @return supported language
     */
    public static SupportedLanguage fromCode(String code) {
        if (code == null || code.isBlank()) {
            return ENGLISH;
        }

        return Arrays.stream(values())
                .filter(language ->
                        language.code.equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElse(ENGLISH);
    }
}