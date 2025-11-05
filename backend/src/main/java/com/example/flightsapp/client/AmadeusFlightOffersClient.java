package com.example.flightsapp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.flightsapp.client.interfaces.FlightOffersClient;
import com.example.flightsapp.client.interfaces.TokenProvider;
import com.example.flightsapp.dtos.input.FlightSearchDTO;

@Service
@Slf4j
public class AmadeusFlightOffersClient implements FlightOffersClient {

    private final HttpClient httpClient;
    private final TokenProvider tokenProvider;
    private final AmadeusApiProperties properties;

    public AmadeusFlightOffersClient(HttpClient httpClient,
                                     TokenProvider tokenProvider,
                                     AmadeusApiProperties properties) {
        this.httpClient = httpClient;
        this.tokenProvider = tokenProvider;
        this.properties = properties;
    }

    @Override
    public String searchFlights(FlightSearchDTO req) {
        try {
            String token = tokenProvider.getValidAccessToken();
            Map<String, String> params = new LinkedHashMap<>();
            params.put("originLocationCode", req.getOrigin());
            params.put("destinationLocationCode", req.getDestination());
            params.put("departureDate", req.getDepartureDate());

            if (req.getReturnDate() != null && !req.getReturnDate().isBlank()) {
                params.put("returnDate", req.getReturnDate());
            }
            if (req.getCurrencyCode() != null && !req.getCurrencyCode().isBlank()) {
                params.put("currencyCode", req.getCurrencyCode());
            }
            if (req.getNonStop() != null) {
                params.put("nonStop", String.valueOf(req.getNonStop()));
            }
            params.put("adults", String.valueOf(req.getAdults() == null ? 1 : req.getAdults()));
            params.put("max", "5");

            String queryString = params.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getFlightSearchUrl() + "?" + queryString))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            log.debug("Calling Amadeus flight-offers API: {}", request.uri());
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Amadeus flight-offers API returned {}", response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            log.error("Error searching flights", e);
            // Return a safe empty result shape so upstream parsing/mapping
            // can treat this as "no offers" instead of throwing.
            return "{\"data\": []}";
        }
    }
}
