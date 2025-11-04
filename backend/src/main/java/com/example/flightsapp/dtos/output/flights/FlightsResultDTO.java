package com.example.flightsapp.dtos.output.flights;

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
        private List<Itinerary> itineraries;
        private List<TravelerPricings> travelerPricings;
        private PriceTotals priceTotals;
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
        private String nextLayover; // human readable, e.g. "1h 20m"
        private String nextLayoverIso; // ISO duration, e.g. "PT1H20M"
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelerPricings{
        private String travelerId;
        private List<FareDetails> fareDetailsBySegment;
        private PriceTravelerDetails priceTravelerDetails;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FareDetails{
        private String segmentId;
        private String cabin;
        private String classTrip;
        private List<Amenities> amenities;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amenities{
        private String description;
        private Boolean isChargeable;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceTravelerDetails{
        private String currency;
        private String total;
        private String base;

    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceTotals{
        private String currency;
        private String total;
        private String base;
        private List<Fees> fees;
        private String grandTotal;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fees{
        private String amount;
        private String type;
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
