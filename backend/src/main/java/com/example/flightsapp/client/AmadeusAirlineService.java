package com.example.flightsapp.client;

import com.example.flightsapp.client.interfaces.AirlineDirectory;
import com.example.flightsapp.client.interfaces.TokenProvider;
import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import com.example.flightsapp.dtos.output.flights.FlightsResultDTO;
import com.example.flightsapp.mapper.AmadeusAirlineMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AmadeusAirlineService implements AirlineDirectory {

    private final HttpClient httpClient;
    private final TokenProvider tokenProvider;
    private final AmadeusApiProperties properties;
    private final AmadeusAirlineMapper airlineMapper;

    public AmadeusAirlineService(HttpClient httpClient,
                                 TokenProvider tokenProvider,
                                 AmadeusApiProperties properties,
                                 AmadeusAirlineMapper airlineMapper) {
        this.httpClient = httpClient;
        this.tokenProvider = tokenProvider;
        this.properties = properties;
        this.airlineMapper = airlineMapper;
    }

    @Override
    public Map<String, AirlineDetailsDTO> getAirlinesForFlight(FlightsResultDTO flightResult) {
        if (flightResult == null || flightResult.getFlightOffers() == null) {
            return new HashMap<>();
        }

        Set<String> uniqueAirlineCodes = flightResult.getFlightOffers().stream()
                .flatMap(offer -> offer.getItineraries().stream())
                .flatMap(itinerary -> itinerary.getSegments().stream())
                .flatMap(segment -> Stream.of(
                        segment.getAirline().getCode(),
                        segment.getOperatingAirline() != null ? segment.getOperatingAirline().getCode() : null
                ))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (uniqueAirlineCodes.isEmpty()) {
            return new HashMap<>();
        }

        String response = getAirlineDetails(uniqueAirlineCodes.toArray(new String[0]));
        List<AirlineDetailsDTO> airlines = airlineMapper.parseAirlineDetailsList(response);

        return airlines.stream()
                .collect(Collectors.toMap(
                        AirlineDetailsDTO::getAirlineCode,
                        airline -> airline,
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public String getAirlineDetails(String... airlineCodes) {
        try {
            String token = tokenProvider.getValidAccessToken();

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
