package com.inventiapp.stocktrack.localization.domain.model.queries;

/**
 * Query used to obtain a language recommendation from an IP address.
 *
 * @param ipAddress client IP address
 * @since 1.0
 */
public record GetLocalizationByIpQuery(
        String ipAddress
) {

    public GetLocalizationByIpQuery {
        if (ipAddress == null || ipAddress.isBlank()) {
            throw new IllegalArgumentException(
                    "IP address cannot be null or blank"
            );
        }

        ipAddress = ipAddress.trim();
    }
}