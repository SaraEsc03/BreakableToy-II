package com.example.flightsapp.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.example.flightsapp.client.AmadeusApiClientService;
import com.example.flightsapp.dtos.input.FlightSearchDTO;

/**
 * FlightSearchController handles flight search requests using Amadeus API based on user input.
 */
@RestController
@RequestMapping("/api/v1/flights")
@Validated
public class FlightSearchController {

    private final AmadeusApiClientService amadeusApiClientService;

    public FlightSearchController(AmadeusApiClientService amadeusApiClientService) {
        this.amadeusApiClientService = amadeusApiClientService;
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
    public String searchFlights(@Valid @ModelAttribute FlightSearchDTO request) {
        // The request object will bind query parameters. Optional fields may be null.
        return amadeusApiClientService.searchFlights(request);
    }
}