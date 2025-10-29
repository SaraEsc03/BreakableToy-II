package com.example.flightsapp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.flightsapp.FlightsResultDTO.AirportInfo;
import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;
import com.example.flightsapp.mapper.AmadeusAirportMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.flightsapp.FlightsResultDTO;
import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import com.example.flightsapp.mapper.AmadeusAirlineMapper;

@Service
@Slf4j
public class AmadeusApiClientService implements TokenProvider, FlightOffersClient, AirportDirectory, AirlineDirectory {

    /**
     * Client service that interacts with the Amadeus APIs.
     *
     * Responsibilities:
     * - Acquire and refresh an OAuth2 access token (client_credentials).
     * - Perform flight search requests and return raw JSON (the mapper layer
     *   is responsible for converting the JSON into DTOs).
     * - Provide airport lookup/autocomplete with a simple in-memory cache to
     *   avoid frequent identical requests.
     */

    private final Cache<String, AirportDetailsDTO> codeCache;
    private final Cache<String, java.util.List<AirportDetailsDTO>> queryCache;
    private final HttpClient httpClient;
    private final AmadeusApiProperties properties;
    private final AmadeusAirportMapper mapper;
    private final AmadeusAirlineMapper airlineMapper;

    private String accessToken;
    private Instant tokenExpiry;

    // Cache tuning constants
    private static final int MAX_STORE_PER_QUERY = 10;

    public AmadeusApiClientService(AmadeusApiProperties properties) {
        this.httpClient = HttpClient.newHttpClient();
        this.properties = properties;
        this.mapper = new AmadeusAirportMapper();
        this.airlineMapper = new AmadeusAirlineMapper();

        // Initialize caches with sensible defaults (tunable)
        this.codeCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(6))
                .maximumSize(10_000)
                .build();

        this.queryCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(30))
                .maximumSize(5_000)
                .build();
    }

    // === AUTHENTICATION ============================================================
    private void authenticate() throws IOException, InterruptedException {
        // Request a new access token using client credentials flow.
        // On success we store the token and compute an expiry timestamp minus
        // a small safety buffer (60s) to avoid using an immediately-expiring
        // token.
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

    @Override
    public String getValidAccessToken() throws IOException, InterruptedException {
        // Double-checked locking: avoid multiple threads performing token
        // refresh simultaneously.
        if (accessToken == null || tokenExpiry == null || Instant.now().isAfter(tokenExpiry)) {
            synchronized (this) {
                if (accessToken == null || tokenExpiry == null || Instant.now().isAfter(tokenExpiry)) {
                    authenticate();
                    log.info("Refreshed Amadeus access token; expires at {}", tokenExpiry);
                } else {
                    log.debug("Access token already refreshed by another thread");
                }
            }
        }
        return accessToken;
    }

    // === FLIGHTS ===============================================================================
    @Override
    public String searchFlights(com.example.flightsapp.dtos.input.FlightSearchDTO req) {
        // Build the Amadeus /v2/shopping/flight-offers request from the input
        // FlightSearchDTO and return the raw JSON response as a String.
        // The method intentionally returns raw JSON so the mapping step can be
        // tested independently.
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

        log.debug("Calling Amadeus flight-offers API: {}", request.uri());
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.debug("Amadeus flight-offers API returned {}", response.statusCode());
        return response.body();
        } catch (IOException | InterruptedException e) {
            log.error("Error searching flights", e);
            return "{}";
        }
    }

    // === AIRPORTS =================================================================================

    // GET AIRPORT DETAILS BY CODE WITH CACHING
    @Override
    public List<AirportDetailsDTO> searchAirportsForAutocomplete(String keyword, int limit) {
        // Return cached autocomplete results when available; otherwise call the
        // Amadeus reference-data API and map the response into
        // AirportDetailsDTO objects. This method also warms the codeCache so
        // individual code lookups can be served quickly later.
    if (keyword == null) keyword = "";
    final String lookupKeyword = keyword;
    String key = lookupKeyword.trim().toLowerCase();

        // Try fast path: return if present in cache
        java.util.List<AirportDetailsDTO> cached = queryCache.getIfPresent(key);
        if (cached != null) {
            log.debug("Autocomplete cache hit for query='{}' ({} results)", key, cached.size());
            return cached.size() <= limit ? cached : cached.subList(0, limit);
        }

        // Atomic load via Caffeine get(key, loader) to avoid duplicate network calls
        java.util.List<AirportDetailsDTO> loaded = queryCache.get(key, k -> {
            log.info("Cache miss for autocomplete query='{}'. Calling Amadeus API.", k);
            String json = searchAirports(lookupKeyword, 0);
            List<AirportDetailsDTO> parsed = mapper.parseAirportList(json);
            if (parsed == null) parsed = Collections.emptyList();
            // Store only top N to avoid large payloads in cache
            if (parsed.size() > MAX_STORE_PER_QUERY) {
                parsed = parsed.subList(0, MAX_STORE_PER_QUERY);
            }
            // Warm codeCache for individual code lookups
            for (AirportDetailsDTO dto : parsed) {
                if (dto.getAirportCode() != null) {
                    codeCache.put(dto.getAirportCode(), dto);
                }
            }
            return parsed;
        });

        return loaded.size() <= limit ? loaded : loaded.subList(0, limit);
    }

    
    // TRANSFORM TO AirportInfo FOR FLIGHT
    @Override
    public AirportInfo getAirportInfoByCode(String code) {
        // Resolve an airport code to a minimal AirportInfo used by the
        // frontend. This method first consults the codeCache and falls back to
        // a live API request if needed. If the airport cannot be resolved we
        // return an AirportInfo with a human-friendly placeholder.
        if (code == null) return new AirportInfo(null, "Unknown Airport");

        // Atomic cache lookup and loader: if missing, call reference-data API for the code
        AirportDetailsDTO dto = codeCache.get(code, k -> {
            log.info("Resolving airport code '{}' via Amadeus API", k);
            String json = searchAirports(k, 0);
            List<AirportDetailsDTO> list = mapper.parseAirportList(json);
            if (list == null || list.isEmpty()) {
                return new AirportDetailsDTO("Unknown Airport", k);
            }
            return list.get(0);
        });

        if (dto == null) return new AirportInfo(code, "Unknown Airport");
        return new AirportInfo(dto.getAirportCode(), dto.getName());
    }
    


    //DIRECT AIRPORTS API CALL 
    @Override
    public String searchAirports(String keyword, int pageoffset) {
        // Direct call to Amadeus reference-data/locations. Returns raw JSON as
        // a String. This method handles token acquisition and basic request
        // parameter encoding; it logs errors and returns an empty JSON object
        // string on failure to avoid throwing across the controller layer.
        try {
            String token = getValidAccessToken();

            // Query params
            String subType = "AIRPORT,CITY";
            String sort = "analytics.travelers.score";
            String view = "LIGHT";

            Map<String, String> params = new LinkedHashMap<>();
            params.put("keyword", keyword != null && !keyword.isBlank() ? keyword : "a"); // Amadeus API requires at
                                                                                          // least one character
            params.put("subType", subType);
            params.put("page[offset]", String.valueOf(pageoffset));
            params.put("sort", sort);
            params.put("view", view);

            String queryString = params.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getApiBaseUrl() + "/v1/reference-data/locations?" + queryString))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

        log.debug("Calling Amadeus locations API: {}", request.uri());
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.debug("Amadeus locations API returned {}", response.statusCode());
        return response.body();

        } catch (IOException | InterruptedException e) {
            log.error("Error searching airports", e);
            return "{}";
        }
    }




   // === AIRLINES =================================================================================
    
    /**
     * Get airline details for all airlines in a flight result.
     * This method extracts all unique airline codes from the segments,
     * fetches their details in a single API call, and returns a map
     * for easy lookup when building the response.
     */
    @Override
    public Map<String, AirlineDetailsDTO> getAirlinesForFlight(FlightsResultDTO flightResult) {
        if (flightResult == null || flightResult.getFlightOffers() == null) {
            return new HashMap<>();
        }

        // Extract unique airline codes from all segments
        Set<String> uniqueAirlineCodes = flightResult.getFlightOffers().stream()
            .flatMap(offer -> offer.getItineraries().stream())
            .flatMap(itinerary -> itinerary.getSegments().stream())
            .flatMap(segment -> Stream.of(
                segment.getAirline().getCode(),
                segment.getOperatingAirline() != null ? segment.getOperatingAirline().getCode() : null
            ))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // If no airlines found, return empty map
        if (uniqueAirlineCodes.isEmpty()) {
            return new HashMap<>();
        }

        // Get all airline details in a single API call
        String response = getAirlineDetails(uniqueAirlineCodes.toArray(new String[0]));
        List<AirlineDetailsDTO> airlines = airlineMapper.parseAirlineDetailsList(response);

        // Convert to map for easy lookup
        return airlines.stream()
            .collect(Collectors.toMap(
                AirlineDetailsDTO::getAirlineCode,
                airline -> airline,
                (existing, replacement) -> existing  // Keep first occurrence on duplicate
            ));
    }

    @Override
    public String getAirlineDetails(String... airlineCodes) {
        // Direct call to Amadeus reference-data/airlines. Returns raw JSON as
        // a String with airline details. Accepts multiple airline codes and
        // combines them into a single request using comma separation.
        try {
            String token = getValidAccessToken();
            
            // Join multiple codes with comma for the API
            String combinedCodes = String.join(",", airlineCodes);
            
            String url = String.format("%s/v1/reference-data/airlines?airlineCodes=%s",
                properties.getApiBaseUrl(),
                URLEncoder.encode(combinedCodes, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

        log.debug("Calling Amadeus airlines API: {}", request.uri());
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.debug("Amadeus airlines API returned {}", response.statusCode());
        return response.body();

        } catch (IOException | InterruptedException e) {
            log.error("Error fetching airline details", e);
            return "{}";
        }
    }
}