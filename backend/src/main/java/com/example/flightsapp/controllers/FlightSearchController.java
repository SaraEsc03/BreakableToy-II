package com.example.flightsapp.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.example.flightsapp.dtos.output.flights.FlightOfferResponseDTO;
import com.example.flightsapp.FlightsResultDTO;
import com.example.flightsapp.mapper.FlightsResultMapper;
import com.example.flightsapp.client.AmadeusApiClientService;
import com.example.flightsapp.dtos.input.FlightSearchDTO;
import com.example.flightsapp.mapper.AmadeusResponseMapper;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * FlightSearchController handles flight search requests using Amadeus API based on user input.
 */
@RestController
@RequestMapping("/api/v1/flights")
@Validated
public class FlightSearchController {

    private final AmadeusApiClientService amadeusApiClientService;
    private final AmadeusResponseMapper amadeusResponseMapper;
    private final FlightsResultMapper flightsResultMapper;

    public FlightSearchController(AmadeusApiClientService amadeusApiClientService,
                                  AmadeusResponseMapper amadeusResponseMapper,
                                  FlightsResultMapper flightsResultMapper) {
        this.amadeusApiClientService = amadeusApiClientService;
        this.amadeusResponseMapper = amadeusResponseMapper;
        this.flightsResultMapper = flightsResultMapper;
    }

    /**
     * Search for flights using the Amadeus API.
     * @param origin The origin airport code (e.g., "LAX").
     * @param destination The destination airport code (e.g., "JFK").
     * @param departureDate The departure date in the format "YYYY-MM-DD".
     * @param adults The number of adults traveling (default is 1).
     * @return A JSON string response containing flight search results.
     */
    @GetMapping("/search")
    public FlightsResultDTO searchFlights(@Valid @ModelAttribute FlightSearchDTO request) {
        // Controller orchestration:
        // 1) call the AmadeusApiClientService to fetch raw JSON
        // 2) map raw JSON to internal Amadeus DTOs with AmadeusResponseMapper
        // 3) convert to frontend FlightsResultDTO with FlightsResultMapper
        // Get raw JSON from client
        String raw = amadeusApiClientService.searchFlights(request);

        // Parse and map to Amadeus DTOs
        JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
        List<FlightOfferResponseDTO> offers = amadeusResponseMapper.mapFlightOffers(json);

        // Transform into frontend FlightsResultDTO (resolving airport names via cache/API)
        return flightsResultMapper.toFlightsResult(offers);
    }
}