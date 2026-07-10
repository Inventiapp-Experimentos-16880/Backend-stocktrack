package com.inventiapp.stocktrack.localization.infrastructure.geolocation;

import com.inventiapp.stocktrack.localization.application.outboundservices.GeoLocationProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Optional;

/**
 * Infrastructure implementation of GeoLocationProvider.
 *
 * It obtains the country associated with a public IP address
 * through an external geolocation service.
 *
 * @since 1.0
 */
@Component
public class IpGeoLocationProvider implements GeoLocationProvider {

    private static final String GEOLOCATION_BASE_URL =
            "https://ipwho.is";

    private final RestClient restClient;

    /**
     * Creates the geolocation provider.
     */
    public IpGeoLocationProvider() {
        this.restClient = RestClient.builder()
                .baseUrl(GEOLOCATION_BASE_URL)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> findCountryCodeByIp(String ipAddress) {
        if (!isPublicIpAddress(ipAddress)) {
            return Optional.empty();
        }

        try {
            var response = restClient
                    .get()
                    .uri("/{ipAddress}", ipAddress)
                    .retrieve()
                    .body(IpWhoIsResponse.class);

            if (response == null
                    || !response.success()
                    || response.countryCode() == null
                    || response.countryCode().isBlank()) {

                return Optional.empty();
            }

            return Optional.of(
                    response.countryCode()
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );

        } catch (RestClientException exception) {
            /*
             * A geolocation failure must not prevent the application
             * from continuing. The query service will use its fallback.
             */
            return Optional.empty();
        }
    }

    /**
     * Validates that the address can be sent to the external provider.
     *
     * Local, private and link-local addresses cannot be geographically
     * resolved through a public IP service.
     *
     * @param ipAddress IP address to validate
     * @return true when the IP is public
     */
    private boolean isPublicIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return false;
        }

        try {
            var address = InetAddress.getByName(ipAddress.trim());

            return !address.isAnyLocalAddress()
                    && !address.isLoopbackAddress()
                    && !address.isSiteLocalAddress()
                    && !address.isLinkLocalAddress();

        } catch (UnknownHostException exception) {
            return false;
        }
    }
}