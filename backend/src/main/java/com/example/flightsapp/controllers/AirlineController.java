package com.example.flightsapp.controllers;

import com.example.flightsapp.client.interfaces.AirlineDirectory;
import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import com.example.flightsapp.mapper.AmadeusAirlineMapper;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving airline information.
 *
 * Endpoint: GET /api/v1/airlines/{code}
 * Returns AirlineDetailsDTO with business name and IATA code
 * for the requested airline.
 */
@RestController
@RequestMapping("/api/v1")
public class AirlineController {

    private final AirlineDirectory airlineDirectory;
    private final AmadeusAirlineMapper airlineMapper;

    public AirlineController(AirlineDirectory airlineDirectory, AmadeusAirlineMapper airlineMapper) {
        this.airlineDirectory = airlineDirectory;
        this.airlineMapper = airlineMapper;
    }

    @GetMapping("/airlines/{code}")
    public ResponseEntity<AirlineDetailsDTO> getAirlineDetails(@PathVariable @Valid String code) {
        String rawResponse = airlineDirectory.getAirlineDetails(code);
        AirlineDetailsDTO airline = airlineMapper.parseAirlineDetails(rawResponse);
        
        if (airline == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(airline);
    }
}
