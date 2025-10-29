package com.example.flightsapp.controllers;

import com.example.flightsapp.client.AirportDirectory;
import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AirportController {

    /**
     * Simple controller exposing a lightweight airport autocomplete endpoint.
     *
     * Endpoint: GET /api/v1/airports/search?q=<term>&limit=<n>
     * Returns a short list of AirportDetailsDTO suitable for showing typeahead
     * suggestions on the frontend.
     *
     * Behavior:
     * - Returns an empty list when the query is empty.
     * - Delegates caching and API calls to AmadeusApiClientService.
     */

    private final AirportDirectory airportDirectory;

    public AirportController(AirportDirectory airportDirectory) {
        this.airportDirectory = airportDirectory;
    }

    @GetMapping("/api/v1/airports/search")
    public List<AirportDetailsDTO> searchAirports(@RequestParam("q") String q,
                                                 @RequestParam(value = "limit", defaultValue = "10") int limit) {
        // Protect callers from empty queries and forward to the client
        if (q == null || q.trim().length() < 1) return List.of();
        return airportDirectory.searchAirportsForAutocomplete(q, limit);
    }
}
