package com.inventiapp.stocktrack.Entities;

import com.inventiapp.stocktrack.localization.domain.model.valueobjects.SupportedLanguage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupportedLanguageTest {

    @Test
    void shouldReturnSpanishFromCode() {
        // Act
        var result = SupportedLanguage.fromCode("es");

        // Assert
        assertEquals(SupportedLanguage.SPANISH, result);
    }

    @Test
    void shouldReturnEnglishFromCode() {
        // Act
        var result = SupportedLanguage.fromCode("en");

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }

    @Test
    void shouldReturnGermanFromCode() {
        // Act
        var result = SupportedLanguage.fromCode("de");

        // Assert
        assertEquals(SupportedLanguage.GERMAN, result);
    }

    @Test
    void shouldReturnFrenchFromCode() {
        // Act
        var result = SupportedLanguage.fromCode("fr");

        // Assert
        assertEquals(SupportedLanguage.FRENCH, result);
    }

    @Test
    void shouldReturnPortugueseFromCode() {
        // Act
        var result = SupportedLanguage.fromCode("pt");

        // Assert
        assertEquals(SupportedLanguage.PORTUGUESE, result);
    }

    @Test
    void shouldReturnItalianFromCode() {
        // Act
        var result = SupportedLanguage.fromCode("it");

        // Assert
        assertEquals(SupportedLanguage.ITALIAN, result);
    }

    @Test
    void shouldReturnJapaneseFromCode() {
        // Act
        var result = SupportedLanguage.fromCode("ja");

        // Assert
        assertEquals(SupportedLanguage.JAPANESE, result);
    }

    @Test
    void shouldIgnoreCodeCase() {
        // Act
        var result = SupportedLanguage.fromCode("FR");

        // Assert
        assertEquals(SupportedLanguage.FRENCH, result);
    }

    @Test
    void shouldReturnEnglishWhenCodeIsNull() {
        // Act
        var result = SupportedLanguage.fromCode(null);

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }

    @Test
    void shouldReturnEnglishWhenCodeIsUnsupported() {
        // Act
        var result = SupportedLanguage.fromCode("ko");

        // Assert
        assertEquals(SupportedLanguage.ENGLISH, result);
    }
}