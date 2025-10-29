package com.example.flightsapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightsResultDTO {

    private List<FlightOffer> flightOffers;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightOffer {
        private String id;
        private String totalPrice;
        private String pricePerTraveler;
        private String currency;
        private List<Itinerary> itineraries;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Itinerary {
        private String initialDepartureDateTime;
        private String finalArrivalDateTime;   // Last arrival of the itinerary
        private String totalDuration;
        private List<Segment> segments;
        private List<StopInfo> stopTimes; // optional field
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Segment {
        private String id;
        private AirportInfo departureAirport;
        private AirportInfo arrivalAirport;
        private String departureDateTime;
        private String arrivalDateTime;
        private AirlineInfo airline;
        private AirlineInfo operatingAirline;
        private String flightNumber;
        private String aircraftType;
        private String duration;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirportInfo {
        private String code;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirlineInfo {
        private String code;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StopInfo {
        private AirportInfo airport;
        private String duration; // duration of stopover at this airport
    }
}
