package com.example.flightsapp;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/airlines")
@RequiredArgsConstructor
public class AirlineController {

    private final AmadeusApiClientService referenceDataService;

    /**
     * Busca información de aerolíneas por código IATA.
     * Ejemplo: GET /api/v1/airlines?codes=BA,IB
     */
    @GetMapping
    public Object getAirlines(@RequestParam String codes) {
        return referenceDataService.getAirlines(codes);
    }
}