package com.example.flightsapp;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AmadeusApiClientService referenceDataService;

    /**
     * Busca aeropuertos por palabra clave (ciudad o código).
     * Ejemplo: GET /api/v1/airports?keyword=MEX
     */
    @GetMapping
    public Object getAirports(@RequestParam String keyword) {
        return referenceDataService.getAirports(keyword);
    }
}
