package com.example.flightsapp.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportDetailsDTO {

    private String type;   
    private String name;
    private String airportCode;
}
