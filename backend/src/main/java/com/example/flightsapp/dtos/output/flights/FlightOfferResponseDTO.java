package com.example.flightsapp.dtos.output.flights;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightOfferResponseDTO {
    private String id;
    private PriceTotalsResponseDTO priceTotals;
    private TravelerPricingsResponseDTO[] travelerPricings;
    private ItineraryDTO[] itineraries;
}
