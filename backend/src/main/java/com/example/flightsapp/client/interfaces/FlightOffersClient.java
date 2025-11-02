package com.example.flightsapp.client.interfaces;

import com.example.flightsapp.dtos.input.FlightSearchDTO;

public interface FlightOffersClient {
    String searchFlights(FlightSearchDTO req);
}
