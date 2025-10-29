package com.example.flightsapp.client;

import com.example.flightsapp.dtos.input.FlightSearchDTO;

public interface FlightOffersClient {
    String searchFlights(FlightSearchDTO req);
}
