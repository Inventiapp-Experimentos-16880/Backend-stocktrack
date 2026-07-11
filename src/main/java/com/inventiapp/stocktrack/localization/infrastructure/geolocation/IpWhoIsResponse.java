package com.inventiapp.stocktrack.localization.infrastructure.geolocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response returned by the external IP geolocation service.
 *
 * This record belongs to the infrastructure layer because its structure
 * depends on an external provider.
 *
 * @param success indicates whether the IP was resolved
 * @param countryCode detected ISO country code
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IpWhoIsResponse(

        boolean success,

        @JsonProperty("country_code")
        String countryCode

) {
}