package com.example.flightsapp.mapper;

import com.example.flightsapp.dtos.output.*;
import com.example.flightsapp.dtos.output.flights.AirportTravelingsInfoDTO;
import com.example.flightsapp.dtos.output.flights.AmenitiesDTO;
import com.example.flightsapp.dtos.output.flights.FareDetailsDTO;
import com.example.flightsapp.dtos.output.flights.FlightOfferResponseDTO;
import com.example.flightsapp.dtos.output.flights.ItineraryDTO;
import com.example.flightsapp.dtos.output.flights.PriceTotalsResponseDTO;
import com.example.flightsapp.dtos.output.flights.PriceTravelerDetailsDTO;
import com.example.flightsapp.dtos.output.flights.SegmentDTO;
import com.example.flightsapp.dtos.output.flights.TravelerPricingsResponseDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AmadeusResponseMapperTest {

    private AmadeusResponseMapper mapper;
    private JsonObject sampleResponse;

    private static final String SAMPLE_API_RESPONSE = """
            {
              "meta": {
                "count": 2,
                "links": {
                  "self": "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=SYD&destinationLocationCode=BKK&departureDate=2025-12-02&adults=1&nonStop=false&max=2"
                }
              },
              "data": [
                {
                  "type": "flight-offer",
                  "id": "1",
                  "source": "GDS",
                  "instantTicketingRequired": false,
                  "nonHomogeneous": false,
                  "oneWay": false,
                  "lastTicketingDate": "2025-10-27",
                  "numberOfBookableSeats": 9,
                  "itineraries": [
                    {
                      "duration": "PT14H30M",
                      "segments": [
                        {
                          "departure": {
                            "iataCode": "SYD",
                            "terminal": "0",
                            "at": "2025-12-02T07:15:00"
                          },
                          "arrival": {
                            "iataCode": "DPS",
                            "terminal": "I",
                            "at": "2025-12-02T10:45:00"
                          },
                          "carrierCode": "OD",
                          "number": "172",
                          "aircraft": {
                            "code": "7M8"
                          },
                          "operating": {
                            "carrierCode": "OD"
                          },
                          "duration": "PT6H30M",
                          "id": "3",
                          "numberOfStops": 0
                        }
                      ]
                    }
                  ],
                  "price": {
                    "currency": "EUR",
                    "total": "202.42",
                    "base": "129.00",
                    "fees": [
                      {
                        "amount": "0.00",
                        "type": "SUPPLIER"
                      }
                    ],
                    "grandTotal": "202.42"
                  },
                  "travelerPricings": [
                    {
                      "travelerId": "1",
                      "fareOption": "STANDARD",
                      "travelerType": "ADULT",
                      "price": {
                        "currency": "EUR",
                        "total": "202.42",
                        "base": "129.00"
                      },
                      "fareDetailsBySegment": [
                        {
                          "segmentId": "3",
                          "cabin": "ECONOMY",
                          "fareBasis": "X1OBSSAU",
                          "class": "X",
                          "amenities": [
                            {
                              "description": "UPTO44LB 20KG BAGGAGE",
                              "isChargeable": true,
                              "amenityType": "BAGGAGE"
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }""";

    @BeforeEach
    void setUp() {
        mapper = new AmadeusResponseMapper();
        sampleResponse = JsonParser.parseString(SAMPLE_API_RESPONSE).getAsJsonObject();
    }

    @Test
    void mapFlightOffers_ShouldMapBasicFlightInformation() {
        // ARRANGE
        // BeforeEach already sets up mapper and sampleResponse

        // ACT
        List<FlightOfferResponseDTO> result = mapper.mapFlightOffers(sampleResponse);

        // ASSERT
        assertThat(result).isNotEmpty();
        FlightOfferResponseDTO offer = result.get(0);
        assertThat(offer.getId()).isEqualTo("1");
    }

    @Test
    void mapFlightOffers_ShouldMapPriceInformation() {
        // ACT
        List<FlightOfferResponseDTO> result = mapper.mapFlightOffers(sampleResponse);

        // ASSERT
        FlightOfferResponseDTO offer = result.get(0);
        PriceTotalsResponseDTO price = offer.getPriceTotals();
        
        assertThat(price).isNotNull();
        assertThat(price.getCurrency()).isEqualTo("EUR");
        assertThat(price.getTotal()).isEqualTo("202.42");
        assertThat(price.getBase()).isEqualTo("129.00");
        assertThat(price.getGrandTotal()).isEqualTo("202.42");
    }

    @Test
    void mapFlightOffers_ShouldMapItinerariesAndSegments() {
        // ACT
        List<FlightOfferResponseDTO> result = mapper.mapFlightOffers(sampleResponse);

        // ASSERT
        FlightOfferResponseDTO offer = result.get(0);
        ItineraryDTO[] itineraries = offer.getItineraries();
        
        assertThat(itineraries).isNotEmpty();
        assertThat(itineraries[0].getTotalDuration()).isEqualTo("PT14H30M");
        
        SegmentDTO[] segments = itineraries[0].getSegments();
        assertThat(segments).isNotEmpty();
        
        SegmentDTO segment = segments[0];
        assertThat(segment.getId()).isEqualTo("3");
        assertThat(segment.getDuration()).isEqualTo("PT6H30M");
        assertThat(segment.getCarrierCode()).isEqualTo("OD");
        assertThat(segment.getNumber()).isEqualTo("172");
        assertThat(segment.getAircraft()).isEqualTo("7M8");
        assertThat(segment.getOperating()).isEqualTo("OD");
    }

    @Test
    void mapFlightOffers_ShouldMapAirportInformation() {
        // ACT
        List<FlightOfferResponseDTO> result = mapper.mapFlightOffers(sampleResponse);

        // ASSERT
        SegmentDTO segment = result.get(0).getItineraries()[0].getSegments()[0];
        
        AirportTravelingsInfoDTO departure = segment.getDeparture();
        assertThat(departure).isNotNull();
        assertThat(departure.getAirlineCode()).isEqualTo("SYD");
        assertThat(departure.getTerminal()).isEqualTo("0");
        assertThat(departure.getDateTime()).isEqualTo("2025-12-02T07:15:00");

        AirportTravelingsInfoDTO arrival = segment.getArrival();
        assertThat(arrival).isNotNull();
        assertThat(arrival.getAirlineCode()).isEqualTo("DPS");
        assertThat(arrival.getTerminal()).isEqualTo("I");
        assertThat(arrival.getDateTime()).isEqualTo("2025-12-02T10:45:00");
    }

    @Test
    void mapFlightOffers_ShouldMapTravelerPricings() {
        // ACT
        List<FlightOfferResponseDTO> result = mapper.mapFlightOffers(sampleResponse);

        // ASSERT
        FlightOfferResponseDTO offer = result.get(0);
        TravelerPricingsResponseDTO[] travelerPricings = offer.getTravelerPricings();
        
        assertThat(travelerPricings).isNotEmpty();
        TravelerPricingsResponseDTO travelerPricing = travelerPricings[0];
        
        assertThat(travelerPricing.getTravelerId()).isEqualTo("1");
        assertThat(travelerPricing.getTravelerType()).isEqualTo("ADULT");
        
        PriceTravelerDetailsDTO priceDetails = travelerPricing.getPriceDetails();
        assertThat(priceDetails).isNotNull();
        assertThat(priceDetails.getCurrency()).isEqualTo("EUR");
        assertThat(priceDetails.getTotal()).isEqualTo("202.42");
        assertThat(priceDetails.getBase()).isEqualTo("129.00");
    }

    @Test
    void mapFlightOffers_ShouldMapFareDetails() {
        // ACT
        List<FlightOfferResponseDTO> result = mapper.mapFlightOffers(sampleResponse);

        // ASSERT
        FlightOfferResponseDTO offer = result.get(0);
        FareDetailsDTO[] fareDetails = offer.getTravelerPricings()[0].getFareDetailsBySegment();
        
        assertThat(fareDetails).isNotEmpty();
        FareDetailsDTO fareDetail = fareDetails[0];
        
        assertThat(fareDetail.getSegmentId()).isEqualTo("3");
        assertThat(fareDetail.getCabin()).isEqualTo("ECONOMY");
        assertThat(fareDetail.getClassTrip()).isEqualTo("X");
        
        AmenitiesDTO[] amenities = fareDetail.getAmenities();
        assertThat(amenities).isNotEmpty();
        assertThat(amenities[0].getDescription()).isEqualTo("UPTO44LB 20KG BAGGAGE");
        assertThat(amenities[0].getIsChargeable()).isTrue();
    }
}