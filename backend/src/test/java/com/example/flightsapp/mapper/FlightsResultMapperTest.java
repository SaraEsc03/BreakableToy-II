package com.example.flightsapp.mapper;

import com.example.flightsapp.dtos.output.flights.FlightsResultDTO;
import com.example.flightsapp.client.interfaces.AirlineDirectory;
import com.example.flightsapp.client.interfaces.AirportDirectory;
import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import com.example.flightsapp.dtos.output.flights.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FlightsResultMapperTest {

    @Test
    void toFlightsResult_shouldMapPriceTotals_andTravelerPricings_andItineraryBasics() {
        // Arrange: mock services used by mapper
        AirportDirectory airportDirectory = Mockito.mock(AirportDirectory.class);
        AirlineDirectory airlineDirectory = Mockito.mock(AirlineDirectory.class);
        when(airportDirectory.getAirportInfoByCode("MEX")).thenReturn(new FlightsResultDTO.AirportInfo("MEX", "MEX Airport"));
        when(airportDirectory.getAirportInfoByCode("LAX")).thenReturn(new FlightsResultDTO.AirportInfo("LAX", "LAX Airport"));
        when(airlineDirectory.getAirlinesForFlight(any(FlightsResultDTO.class))).thenReturn(Collections.<String, AirlineDetailsDTO>emptyMap());

        FlightsResultMapper mapper = new FlightsResultMapper(airportDirectory, airlineDirectory);

        // Build a minimal FlightOfferResponseDTO tree
        AirportTravelingsInfoDTO dep = new AirportTravelingsInfoDTO("MEX", "T1", "2025-12-25T10:00:00");
        AirportTravelingsInfoDTO arr = new AirportTravelingsInfoDTO("LAX", "T2", "2025-12-25T12:30:00");

        SegmentDTO seg = new SegmentDTO();
        seg.setId("s1");
        seg.setDeparture(dep);
        seg.setArrival(arr);
        seg.setCarrierCode("XX");
        seg.setOperating("XX");
        seg.setNumber("1234");
        seg.setAircraft("320");
        seg.setDuration("PT2H30M");

        ItineraryDTO iti = new ItineraryDTO();
        iti.setSegments(new SegmentDTO[]{seg});
        iti.setTotalDuration("PT2H30M");

        // Price totals with a single fee
        FeesResponseDTO fee = new FeesResponseDTO("0.00", "SUPPLIER");
        PriceTotalsResponseDTO totals = new PriceTotalsResponseDTO("EUR", "100.00", "80.00", new FeesResponseDTO[]{fee}, "100.00");

        // Traveler pricing: price + one fare details entry with one amenity
        PriceTravelerDetailsDTO priceTraveler = new PriceTravelerDetailsDTO("EUR", "100.00", "80.00");
        AmenitiesDTO amenity = new AmenitiesDTO("Seat selection", true);
        FareDetailsDTO fare = new FareDetailsDTO("s1", "ECONOMY", "Y", new AmenitiesDTO[]{amenity});
        TravelerPricingsResponseDTO tp = new TravelerPricingsResponseDTO("1", "ADULT", priceTraveler, new FareDetailsDTO[]{fare});

        FlightOfferResponseDTO offer = new FlightOfferResponseDTO("offer-1", totals, new TravelerPricingsResponseDTO[]{tp}, new ItineraryDTO[]{iti});

        // Act
        FlightsResultDTO out = mapper.toFlightsResult(List.of(offer));

        // Assert
        assertThat(out.getFlightOffers()).hasSize(1);
        FlightsResultDTO.FlightOffer outOffer = out.getFlightOffers().get(0);

        // Price totals
        assertThat(outOffer.getPriceTotals()).isNotNull();
        assertThat(outOffer.getPriceTotals().getCurrency()).isEqualTo("EUR");
        assertThat(outOffer.getPriceTotals().getTotal()).isEqualTo("100.00");
        assertThat(outOffer.getPriceTotals().getBase()).isEqualTo("80.00");
        assertThat(outOffer.getPriceTotals().getGrandTotal()).isEqualTo("100.00");
        assertThat(outOffer.getPriceTotals().getFees()).hasSize(1);
        assertThat(outOffer.getPriceTotals().getFees().get(0).getType()).isEqualTo("SUPPLIER");

        // Traveler pricings
        assertThat(outOffer.getTravelerPricings()).hasSize(1);
        FlightsResultDTO.TravelerPricings outTp = outOffer.getTravelerPricings().get(0);
        assertThat(outTp.getTravelerId()).isEqualTo("1");
        assertThat(outTp.getPriceTravelerDetails()).isNotNull();
        assertThat(outTp.getPriceTravelerDetails().getCurrency()).isEqualTo("EUR");
        assertThat(outTp.getPriceTravelerDetails().getTotal()).isEqualTo("100.00");
        assertThat(outTp.getPriceTravelerDetails().getBase()).isEqualTo("80.00");

        // Fare details (representative first item)
        assertThat(outTp.getFareDetailsBySegment()).isNotNull();
        assertThat(outTp.getFareDetailsBySegment().getSegmentId()).isEqualTo("s1");
        assertThat(outTp.getFareDetailsBySegment().getCabin()).isEqualTo("ECONOMY");
        assertThat(outTp.getFareDetailsBySegment().getClassTrip()).isEqualTo("Y");
        assertThat(outTp.getFareDetailsBySegment().getAmenities()).hasSize(1);
        assertThat(outTp.getFareDetailsBySegment().getAmenities().get(0).getDescription()).isEqualTo("Seat selection");

        // Itinerary basics
        assertThat(outOffer.getItineraries()).hasSize(1);
        FlightsResultDTO.Itinerary outIti = outOffer.getItineraries().get(0);
        assertThat(outIti.getSegments()).hasSize(1);
        assertThat(outIti.getInitialDepartureDateTime()).isEqualTo("2025-12-25T10:00:00");
        assertThat(outIti.getFinalArrivalDateTime()).isEqualTo("2025-12-25T12:30:00");
        // Humanized durations are non-empty
        assertThat(outIti.getTotalDuration()).isNotBlank();
        assertThat(outIti.getSegments().get(0).getDuration()).isNotBlank();
        // Airports resolved via service
        assertThat(outIti.getSegments().get(0).getDepartureAirport().getName()).isEqualTo("MEX Airport");
        assertThat(outIti.getSegments().get(0).getArrivalAirport().getName()).isEqualTo("LAX Airport");
    }
}
