package com.example.flightsapp;
import org.springframework.web.bind.annotation.*;

/**
 * FlightSearchController handles flight search requests using Amadeus API based on user input.
 */
@RestController
@RequestMapping("/api/v1/flights")
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
    public String searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam(defaultValue = "1") int adults) {

        return amadeusApiClientService.searchFlights(origin, destination, departureDate, adults);
    }
}