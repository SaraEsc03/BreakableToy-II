package com.example.flightsapp.controllers;

import com.example.flightsapp.client.interfaces.AirportDirectory;
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
     * - Delegates caching and API calls to AirportDirectory.
     */

    private final AirportDirectory airportDirectory;

    public AirportController(AirportDirectory airportDirectory) {
        this.airportDirectory = airportDirectory;
    }

    @GetMapping("/api/v1/airports/search")
    public List<AirportDetailsDTO> searchAirports(
            @RequestParam("q") String q,
            @RequestParam(value = "limit", required = false) String limitParam) {
        // Sanitize and parse limit defensively: accept values like "10" or "10." and clamp to [1, 50]
        int limit = 10; // default
        if (limitParam != null) {
            try {
                String digits = limitParam.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    limit = Integer.parseInt(digits);
                }
            } catch (NumberFormatException ignored) { /* keep default */ }
        }
        if (limit <= 0) limit = 10;
        if (limit > 50) limit = 50;

        // Protect callers from empty queries and forward to the client
        if (q == null || q.trim().length() < 1) return List.of();
        return airportDirectory.searchAirportsForAutocomplete(q, limit);
    }
}
