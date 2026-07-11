package com.inventiapp.stocktrack.integration;

import com.inventiapp.stocktrack.localization.domain.model.queries.GetLocalizationByIpQuery;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.LocalizationResult;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.LocalizationSource;
import com.inventiapp.stocktrack.localization.domain.model.valueobjects.SupportedLanguage;
import com.inventiapp.stocktrack.localization.domain.services.LocalizationQueryService;
import com.inventiapp.stocktrack.localization.interfaces.rest.controllers.LocalizationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LocalizationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LocalizationQueryService localizationQueryService;

    @InjectMocks
    private LocalizationController localizationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(localizationController)
                .build();
    }

    @Test
    void shouldReturnLocalizationRecommendation() throws Exception {
        // Arrange
        var localizationResult = new LocalizationResult(
                "DE",
                SupportedLanguage.GERMAN,
                LocalizationSource.IP
        );

        when(localizationQueryService.handle(
                any(GetLocalizationByIpQuery.class)
        )).thenReturn(localizationResult);

        // Act and Assert
        mockMvc.perform(
                        get("/api/v1/localization")
                                .header(
                                        "X-Forwarded-For",
                                        "8.8.8.8, 10.0.0.1"
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryCode").value("DE"))
                .andExpect(
                        jsonPath("$.recommendedLanguage").value("de")
                )
                .andExpect(jsonPath("$.source").value("IP"));

        var queryCaptor = ArgumentCaptor.forClass(
                GetLocalizationByIpQuery.class
        );

        verify(
                localizationQueryService,
                times(1)
        ).handle(queryCaptor.capture());

        assertEquals(
                "8.8.8.8",
                queryCaptor.getValue().ipAddress()
        );
    }

    @Test
    void shouldReturnFallbackLocalization() throws Exception {
        // Arrange
        var localizationResult = new LocalizationResult(
                null,
                SupportedLanguage.ENGLISH,
                LocalizationSource.FALLBACK
        );

        when(localizationQueryService.handle(
                any(GetLocalizationByIpQuery.class)
        )).thenReturn(localizationResult);

        // Act and Assert
        mockMvc.perform(
                        get("/api/v1/localization")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryCode").isEmpty())
                .andExpect(
                        jsonPath("$.recommendedLanguage").value("en")
                )
                .andExpect(
                        jsonPath("$.source").value("FALLBACK")
                );

        verify(
                localizationQueryService,
                times(1)
        ).handle(any(GetLocalizationByIpQuery.class));
    }
}