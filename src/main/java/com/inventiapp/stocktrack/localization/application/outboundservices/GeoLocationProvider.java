package com.inventiapp.stocktrack.localization.application.outboundservices;

import java.util.Optional;

/**
 * Outbound service used to resolve the country associated with an IP.
 *
 * The application layer depends on this abstraction instead of depending
 * directly on an external geolocation provider.
 *
 * @since 1.0
 */
public interface GeoLocationProvider {

    /**
     * Resolves the ISO country code associated with an IP address.
     *
     * @param ipAddress client IP address
     * @return detected country code when available
     */
    Optional<String> findCountryCodeByIp(String ipAddress);
}