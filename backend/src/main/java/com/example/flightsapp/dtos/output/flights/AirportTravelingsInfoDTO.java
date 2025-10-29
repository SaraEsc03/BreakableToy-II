package com.example.flightsapp.dtos.output.flights;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportTravelingsInfoDTO {

    private String airlineCode;   // maps to "iataCode"
    private String terminal;
    private String dateTime;  // maps to "at"
}
