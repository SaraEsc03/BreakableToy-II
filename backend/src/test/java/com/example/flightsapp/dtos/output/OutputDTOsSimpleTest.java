package com.example.flightsapp.dtos.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutputDTOsSimpleTest {

    @Test
    void basicOutputDtos_settersAndGetters_workAsExpected() {
        // ARRANGE
        PriceTotalsResponseDTO price = new PriceTotalsResponseDTO();
        FeesResponseDTO fee = new FeesResponseDTO();

        // ACT
        price.setCurrency("EUR");
        price.setTotal("100.00");
        price.setBase("80.00");
        price.setGrandTotal("100.00");

        
        fee.setAmount("0.00");
        fee.setType("SUPPLIER");
        price.setFees(new FeesResponseDTO[]{fee});

        // ASSERT
        assertThat(price.getCurrency()).isEqualTo("EUR");
        assertThat(price.getTotal()).isEqualTo("100.00");
        assertThat(price.getFees()).hasSize(1);
        assertThat(price.getFees()[0].getType()).isEqualTo("SUPPLIER");

        // Airport travel info (departure / arrival)
        AirportTravelingsInfoDTO departure = new AirportTravelingsInfoDTO();
        departure.setAirlineCode("LAX");
        departure.setTerminal("2");
        departure.setDateTime("2025-12-01T09:00:00");

        AirportTravelingsInfoDTO arrival = new AirportTravelingsInfoDTO();
        arrival.setAirlineCode("JFK");
        arrival.setTerminal("1");
        arrival.setDateTime("2025-12-01T17:30:00");

        assertThat(departure.getAirlineCode()).isEqualTo("LAX");
        assertThat(arrival.getAirlineCode()).isEqualTo("JFK");

        // Segment with nested airport info
        SegmentDTO segment = new SegmentDTO();
        segment.setId("seg-1");
        segment.setCarrierCode("XX");
        segment.setNumber("123");
        segment.setAircraft("738");
        segment.setOperating("XX");
        segment.setDuration("PT2H");
        segment.setDeparture(departure);
        segment.setArrival(arrival);

        assertThat(segment.getDeparture()).isNotNull();
        assertThat(segment.getArrival().getAirlineCode()).isEqualTo("JFK");

        // Itinerary
        ItineraryDTO itinerary = new ItineraryDTO();
        itinerary.setTotalDuration("PT2H");
        itinerary.setSegments(new SegmentDTO[]{segment});

        assertThat(itinerary.getSegments()).hasSize(1);
        assertThat(itinerary.getTotalDuration()).isEqualTo("PT2H");

        // Amenities and fare details
        AmenitiesDTO amenity = new AmenitiesDTO();
        amenity.setDescription("Seat selection");
        amenity.setIsChargeable(true);

        FareDetailsDTO fare = new FareDetailsDTO();
        fare.setSegmentId("seg-1");
        fare.setCabin("ECONOMY");
        fare.setClassTrip("Y");
        fare.setAmenities(new AmenitiesDTO[]{amenity});

        assertThat(fare.getAmenities()).hasSize(1);
        assertThat(fare.getClassTrip()).isEqualTo("Y");

        // Traveler pricing
        PriceTravelerDetailsDTO pt = new PriceTravelerDetailsDTO();
        pt.setCurrency("EUR");
        pt.setTotal("100.00");
        pt.setBase("80.00");

        TravelerPricingsResponseDTO traveler = new TravelerPricingsResponseDTO();
        traveler.setTravelerId("1");
        traveler.setTravelerType("ADULT");
        traveler.setPriceDetails(pt);
        traveler.setFareDetailsBySegment(new FareDetailsDTO[]{fare});

        assertThat(traveler.getPriceDetails().getTotal()).isEqualTo("100.00");
        assertThat(traveler.getFareDetailsBySegment()).hasSize(1);

        // Flight offer with arrays of itineraries and traveler pricings
        FlightOfferResponseDTO offer = new FlightOfferResponseDTO();
        offer.setId("offer-1");
        offer.setPriceTotals(price);
        offer.setItineraries(new ItineraryDTO[]{itinerary});
        offer.setTravelerPricings(new TravelerPricingsResponseDTO[]{traveler});

        assertThat(offer.getId()).isEqualTo("offer-1");
        assertThat(offer.getPriceTotals()).isNotNull();
        assertThat(offer.getItineraries()).hasSize(1);
        assertThat(offer.getTravelerPricings()).hasSize(1);
    }
}