package com.example.flightsapp.dtos.input;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for airport or city search requests to Amadeus API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportSearchDTO {

    // We’ll fix this as CITY for now; can be AIRPORT later if needed
    private String subType = "CITY";

    @NotBlank(message = "Keyword is required to search airports or cities")
    private String keyword;

    // LIGHT returns a smaller response (default)
    private String view = "LIGHT";
}
