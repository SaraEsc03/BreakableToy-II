package com.example.flightsapp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flightsapp.client.AirportDirectory;
import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;

@RestController
@RequestMapping("/api")
public class AirportSearchController {

    /**
     * Alternate airport search controller used by some tests/endpoints.
     *
     * Exposes: GET /api/airports?keyword=...&pageoffset=...
     * This controller simply delegates to the AmadeusApiClientService and
     * returns the mapped AirportDetailsDTO list.
     */

    private final AirportDirectory airportDirectory;

    public AirportSearchController(AirportDirectory airportDirectory) {
        this.airportDirectory = airportDirectory;
    }

    @GetMapping("/airports")
     public ResponseEntity<List<AirportDetailsDTO>> getAirports(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int pageoffset)
            {

        List<AirportDetailsDTO> results = airportDirectory.searchAirportsForAutocomplete(keyword, pageoffset);
        return ResponseEntity.ok(results);
    }
}
