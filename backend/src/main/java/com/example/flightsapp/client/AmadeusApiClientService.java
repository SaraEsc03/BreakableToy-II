package com.example.flightsapp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AmadeusApiClientService {

    private final HttpClient httpClient;
    private final AmadeusApiProperties properties;

    private String accessToken;
    private Instant tokenExpiry;

    public AmadeusApiClientService(AmadeusApiProperties properties) {
        this.httpClient = HttpClient.newHttpClient();
        this.properties = properties;
    }

    private void authenticate() throws IOException, InterruptedException {
        String form = "grant_type=client_credentials"
                + "&client_id=" + URLEncoder.encode(properties.getClientId(), StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(properties.getClientSecret(), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getTokenUrl()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to authenticate with Amadeus API: " + response.body());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        this.accessToken = json.get("access_token").getAsString();
        int expiresIn = json.get("expires_in").getAsInt();
        this.tokenExpiry = Instant.now().plusSeconds(expiresIn - 60);
    }

    private String getValidAccessToken() throws IOException, InterruptedException {
        if (accessToken == null || tokenExpiry == null || Instant.now().isAfter(tokenExpiry)) {
            authenticate();
        }
        return accessToken;
    }

    // === Flights ===
    public String searchFlights(com.example.flightsapp.dtos.input.FlightSearchDTO req) {
        try {
            String token = getValidAccessToken();
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

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            log.error("Error searching flights", e);
            return "{}";
        }
    }

    // === Airports ===
    public String searchAirports(String keyword, int pageoffset) {
        try {
            String token = getValidAccessToken();

            // Query params
            String subType = "AIRPORT,CITY";
            String sort= "analytics.travelers.score";
            String view= "LIGHT";
            
            Map<String, String> params = new LinkedHashMap<>();
            params.put("keyword", keyword != null && !keyword.isBlank() ? keyword : "a"); // Amadeus API requires at least one character
            params.put("subType", subType);
            params.put("page[offset]", String.valueOf(pageoffset));
            params.put("sort",sort);
            params.put("view",view);

            String queryString = params.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getApiBaseUrl() + "/v1/reference-data/locations?" + queryString))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (IOException | InterruptedException e) {
            log.error("Error searching airports", e);
            return "{}";
        }
    }

}
