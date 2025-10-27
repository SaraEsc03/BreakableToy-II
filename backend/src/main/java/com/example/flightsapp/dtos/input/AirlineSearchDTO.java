package com.example.flightsapp.dtos.input;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for airline search requests to Amadeus API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirlineSearchDTO {

    @NotBlank(message = "Airline code is required")
    private String code;
}
