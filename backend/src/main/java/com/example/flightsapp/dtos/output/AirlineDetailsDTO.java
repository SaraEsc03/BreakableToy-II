package com.example.flightsapp.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents airline information returned by the Amadeus API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirlineDetailsDTO {

    private String airlineCode;   // maps to "iataCode"
    private String businessName;
}
