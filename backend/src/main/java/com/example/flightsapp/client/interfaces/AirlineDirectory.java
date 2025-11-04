package com.example.flightsapp.client.interfaces;

import com.example.flightsapp.dtos.output.flights.FlightsResultDTO;
import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import java.util.Map;

public interface AirlineDirectory {
    String getAirlineDetails(String... airlineCodes);
    Map<String, AirlineDetailsDTO> getAirlinesForFlight(FlightsResultDTO flightResult);
}
