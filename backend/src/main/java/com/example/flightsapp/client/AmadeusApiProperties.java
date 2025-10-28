package com.example.flightsapp.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "amadeus.api")
@Getter
@Setter
public class AmadeusApiProperties {
    /**
     * Configuration properties holder for Amadeus API integration.
     *
     * Populated from application.properties (prefix amadeus.api).
     * Example properties:
     * amadeus.api.client-id, amadeus.api.client-secret, amadeus.api.token-url,
     * amadeus.api.flight-search-url, amadeus.api.api-base-url
     */
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String flightSearchUrl;
    private String apiBaseUrl;  // <--- CHECK THIS NAME
}
