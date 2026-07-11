package com.inventiapp.stocktrack.localization.interfaces.rest.controllers;

import com.inventiapp.stocktrack.localization.domain.model.queries.GetLocalizationByIpQuery;
import com.inventiapp.stocktrack.localization.domain.services.LocalizationQueryService;
import com.inventiapp.stocktrack.localization.interfaces.rest.resources.LocalizationResource;
import com.inventiapp.stocktrack.localization.interfaces.rest.transform.LocalizationResourceFromResultAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for localization detection.
 *
 * This controller obtains the client IP address and returns
 * a language recommendation based on the detected country.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(
        value = "/api/v1/localization",
        produces = APPLICATION_JSON_VALUE
)
@Tag(
        name = "Localization",
        description = "Endpoints for country and language detection"
)
public class LocalizationController {

    private final LocalizationQueryService localizationQueryService;

    /**
     * Constructor for LocalizationController.
     *
     * @param localizationQueryService localization query service
     */
    public LocalizationController(
            LocalizationQueryService localizationQueryService
    ) {
        this.localizationQueryService = localizationQueryService;
    }

    /**
     * Gets the language recommendation for the current client.
     *
     * @param request current HTTP request
     * @return localization recommendation
     */
    @Operation(
            summary = "Get localization recommendation",
            description = """
                    Detects the client country through its IP address and
                    returns the recommended language for the application.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Localization recommendation obtained"
            )
    })
    @GetMapping
    public ResponseEntity<LocalizationResource> getLocalization(
            HttpServletRequest request
    ) {
        var clientIp = resolveClientIp(request);

        var query = new GetLocalizationByIpQuery(clientIp);

        var result = localizationQueryService.handle(query);

        var resource =
                LocalizationResourceFromResultAssembler
                        .toResourceFromResult(result);

        return ResponseEntity.ok(resource);
    }

    /**
     * Resolves the original client IP address.
     *
     * X-Forwarded-For is used when the application is behind
     * a reverse proxy or a cloud deployment platform.
     *
     * @param request current HTTP request
     * @return resolved client IP
     */
    private String resolveClientIp(HttpServletRequest request) {
        var forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        var realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}