package com.example.flightsapp.client;

import com.example.flightsapp.client.interfaces.AirportDirectory;
import com.example.flightsapp.client.interfaces.TokenProvider;
import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;
import com.example.flightsapp.dtos.output.flights.FlightsResultDTO.AirportInfo;
import com.example.flightsapp.mapper.AmadeusAirportMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AmadeusAirportService implements AirportDirectory {

    private static final int MAX_STORE_PER_QUERY = 10;

    private final Cache<String, AirportDetailsDTO> codeCache;
    private final Cache<String, List<AirportDetailsDTO>> queryCache;
    private final HttpClient httpClient;
    private final TokenProvider tokenProvider;
    private final AmadeusApiProperties properties;
    private final AmadeusAirportMapper mapper;

    public AmadeusAirportService(HttpClient httpClient,
                                 TokenProvider tokenProvider,
                                 AmadeusApiProperties properties,
                                 AmadeusAirportMapper mapper) {
        this.httpClient = httpClient;
        this.tokenProvider = tokenProvider;
        this.properties = properties;
        this.mapper = mapper;

        this.codeCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(6))
                .maximumSize(10_000)
                .build();

        this.queryCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(30))
                .maximumSize(5_000)
                .build();
    }

    @Override
    public List<AirportDetailsDTO> searchAirportsForAutocomplete(String keyword, int limit) {
        if (keyword == null) keyword = "";
        final String lookupKeyword = keyword;
        String key = lookupKeyword.trim().toLowerCase();

        List<AirportDetailsDTO> cached = queryCache.getIfPresent(key);
        if (cached != null) {
            log.debug("Autocomplete cache hit for query='{}' ({} results)", key, cached.size());
            return cached.size() <= limit ? cached : cached.subList(0, limit);
        }

        List<AirportDetailsDTO> loaded = queryCache.get(key, k -> {
            log.info("Cache miss for autocomplete query='{}'. Calling Amadeus API.", k);
            String json = searchAirports(lookupKeyword, 0);
            List<AirportDetailsDTO> parsed = mapper.parseAirportList(json);
            if (parsed == null) parsed = Collections.emptyList();
            if (parsed.size() > MAX_STORE_PER_QUERY) {
                parsed = parsed.subList(0, MAX_STORE_PER_QUERY);
            }
            for (AirportDetailsDTO dto : parsed) {
                if (dto.getAirportCode() != null) {
                    codeCache.put(dto.getAirportCode(), dto);
                }
            }
            return parsed;
        });

        return loaded.size() <= limit ? loaded : loaded.subList(0, limit);
    }

    @Override
    public AirportInfo getAirportInfoByCode(String code) {
        if (code == null) return new AirportInfo(null, "Unknown Airport");

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

    @Override
    public String searchAirports(String keyword, int pageoffset) {
        try {
            String token = tokenProvider.getValidAccessToken();

            String subType = "AIRPORT,CITY";
            String sort = "analytics.travelers.score";
            String view = "LIGHT";

            Map<String, String> params = new LinkedHashMap<>();
            params.put("keyword", keyword != null && !keyword.isBlank() ? keyword : "a");
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
}
