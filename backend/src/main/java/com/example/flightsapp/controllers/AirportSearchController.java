package com.example.flightsapp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flightsapp.client.AmadeusApiClientService;

@RestController
@RequestMapping("/api")
public class AirportSearchController {

    private final AmadeusApiClientService clientService;

    public AirportSearchController(AmadeusApiClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/airports")
    public ResponseEntity<String> getAirports(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int pageoffset)
            {

        String result = clientService.searchAirports(keyword, pageoffset);
        return ResponseEntity.ok(result);
    }
}
