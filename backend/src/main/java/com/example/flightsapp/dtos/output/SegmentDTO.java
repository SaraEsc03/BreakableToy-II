package com.example.flightsapp.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SegmentDTO {
    private AirportTravelingsInfoDTO departure;
    private AirportTravelingsInfoDTO arrival;
    private String carrierCode;
    private String number;
    private String aircraft; //// maps to "aircraft.code"
    private String operating; // maps to "operating.carrierCode"
    private String duration;
    private String id; //number of the segment inside itinerary
}
