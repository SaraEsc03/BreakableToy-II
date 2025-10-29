package com.example.flightsapp.client;

import com.example.flightsapp.FlightsResultDTO.AirportInfo;
import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;
import java.util.List;

public interface AirportDirectory {
    String searchAirports(String keyword, int pageoffset);
    List<AirportDetailsDTO> searchAirportsForAutocomplete(String keyword, int limit);
    AirportInfo getAirportInfoByCode(String code);
}
