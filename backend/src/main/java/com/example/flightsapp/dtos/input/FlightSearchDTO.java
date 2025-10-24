package com.example.flightsapp.dtos.input;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for binding flight search request query parameters.
 * Can be bound from GET query parameters using @ModelAttribute in the controller.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchDTO {

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotBlank
    private String departureDate; // YYYY-MM-DD

    // optional
    private String returnDate; // YYYY-MM-DD

    // optional, e.g. "USD"
    private String currency;

    // optional: if true, request non-stop itineraries only
    private Boolean nonStop;

    // default to 1 when not provided
    private Integer adults = 1;
}
