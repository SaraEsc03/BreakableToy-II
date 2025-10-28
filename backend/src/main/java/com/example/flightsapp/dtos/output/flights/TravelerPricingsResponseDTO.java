package com.example.flightsapp.dtos.output.flights;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelerPricingsResponseDTO {
    private String travelerId;
    private String travelerType;
    private PriceTravelerDetailsDTO priceDetails;
    private FareDetailsDTO[] fareDetailsBySegment;
}
