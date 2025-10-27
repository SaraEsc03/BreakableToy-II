package com.example.flightsapp.mapper;

import com.example.flightsapp.dtos.output.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AmadeusResponseMapper {

    public List<FlightOfferResponseDTO> mapFlightOffers(JsonObject amadeusResponse) {
        List<FlightOfferResponseDTO> flightOffers = new ArrayList<>();
        JsonArray data = amadeusResponse.getAsJsonArray("data");

        for (JsonElement element : data) {
            JsonObject offer = element.getAsJsonObject();
            FlightOfferResponseDTO flightOffer = mapFlightOffer(offer);
            flightOffers.add(flightOffer);
        }

        return flightOffers;
    }

    private FlightOfferResponseDTO mapFlightOffer(JsonObject offer) {
        FlightOfferResponseDTO dto = new FlightOfferResponseDTO();
        dto.setId(offer.get("id").getAsString());

        // Map price totals
        if (offer.has("price")) {
            dto.setPriceTotals(mapPrice(offer.getAsJsonObject("price")));
        }

        // Map itineraries
        if (offer.has("itineraries")) {
            JsonArray itineraries = offer.getAsJsonArray("itineraries");
            ItineraryDTO[] itineraryDTOs = new ItineraryDTO[itineraries.size()];
            
            for (int i = 0; i < itineraries.size(); i++) {
                JsonObject itinerary = itineraries.get(i).getAsJsonObject();
                itineraryDTOs[i] = mapItinerary(itinerary);
            }
            
            dto.setItineraries(itineraryDTOs);
        }

        // Map traveler pricings
        if (offer.has("travelerPricings")) {
            JsonArray travelerPricings = offer.getAsJsonArray("travelerPricings");
            TravelerPricingsResponseDTO[] travelerPricingDTOs = new TravelerPricingsResponseDTO[travelerPricings.size()];
            
            for (int i = 0; i < travelerPricings.size(); i++) {
                JsonObject travelerPricing = travelerPricings.get(i).getAsJsonObject();
                travelerPricingDTOs[i] = mapTravelerPricing(travelerPricing);
            }
            
            dto.setTravelerPricings(travelerPricingDTOs);
        }

        return dto;
    }

    private PriceTotalsResponseDTO mapPrice(JsonObject price) {
        PriceTotalsResponseDTO priceDTO = new PriceTotalsResponseDTO();
        priceDTO.setCurrency(price.get("currency").getAsString());
        priceDTO.setTotal(price.get("total").getAsString());
        priceDTO.setBase(price.get("base").getAsString());
        priceDTO.setGrandTotal(price.get("grandTotal").getAsString());
        return priceDTO;
    }

    private ItineraryDTO mapItinerary(JsonObject itinerary) {
        ItineraryDTO dto = new ItineraryDTO();
        dto.setTotalDuration(itinerary.get("duration").getAsString());

        if (itinerary.has("segments")) {
            JsonArray segments = itinerary.getAsJsonArray("segments");
            SegmentDTO[] segmentDTOs = new SegmentDTO[segments.size()];
            
            for (int i = 0; i < segments.size(); i++) {
                JsonObject segment = segments.get(i).getAsJsonObject();
                segmentDTOs[i] = mapSegment(segment);
            }
            
            dto.setSegments(segmentDTOs);
        }

        return dto;
    }

    private SegmentDTO mapSegment(JsonObject segment) {
        SegmentDTO dto = new SegmentDTO();
        dto.setId(segment.get("id").getAsString());
        dto.setDuration(segment.get("duration").getAsString());
        
        // Map departure and arrival info
        if (segment.has("departure")) {
            dto.setDeparture(mapAirportInfo(segment.getAsJsonObject("departure")));
        }
        if (segment.has("arrival")) {
            dto.setArrival(mapAirportInfo(segment.getAsJsonObject("arrival")));
        }

        // Map carrier info
        dto.setCarrierCode(segment.get("carrierCode").getAsString());
        dto.setNumber(segment.get("number").getAsString());

        // Map aircraft code
        if (segment.has("aircraft") && segment.getAsJsonObject("aircraft").has("code")) {
            dto.setAircraft(segment.getAsJsonObject("aircraft").get("code").getAsString());
        }

        // Map operating carrier
        if (segment.has("operating") && segment.getAsJsonObject("operating").has("carrierCode")) {
            dto.setOperating(segment.getAsJsonObject("operating").get("carrierCode").getAsString());
        }

        return dto;
    }

    private TravelerPricingsResponseDTO mapTravelerPricing(JsonObject travelerPricing) {
        TravelerPricingsResponseDTO dto = new TravelerPricingsResponseDTO();
        dto.setTravelerId(travelerPricing.get("travelerId").getAsString());
        dto.setTravelerType(travelerPricing.get("travelerType").getAsString());
        
        // Map price details
        if (travelerPricing.has("price")) {
            dto.setPriceDetails(mapPriceTravelerDetails(travelerPricing.getAsJsonObject("price")));
        }

        // Map fare details by segment
        if (travelerPricing.has("fareDetailsBySegment")) {
            JsonArray fareDetails = travelerPricing.getAsJsonArray("fareDetailsBySegment");
            FareDetailsDTO[] fareDetailsDTOs = new FareDetailsDTO[fareDetails.size()];
            
            for (int i = 0; i < fareDetails.size(); i++) {
                JsonObject fareDetail = fareDetails.get(i).getAsJsonObject();
                fareDetailsDTOs[i] = mapFareDetails(fareDetail);
            }
            
            dto.setFareDetailsBySegment(fareDetailsDTOs);
        }

        return dto;
    }

    private PriceTravelerDetailsDTO mapPriceTravelerDetails(JsonObject price) {
        PriceTravelerDetailsDTO dto = new PriceTravelerDetailsDTO();
        dto.setCurrency(price.get("currency").getAsString());
        dto.setTotal(price.get("total").getAsString());
        dto.setBase(price.get("base").getAsString());
        return dto;
    }

    private FareDetailsDTO mapFareDetails(JsonObject fareDetail) {
        FareDetailsDTO dto = new FareDetailsDTO();
        dto.setSegmentId(fareDetail.get("segmentId").getAsString());
        dto.setCabin(fareDetail.get("cabin").getAsString());
        dto.setClassTrip(fareDetail.get("class").getAsString());

        // Map amenities
        if (fareDetail.has("amenities")) {
            JsonArray amenities = fareDetail.getAsJsonArray("amenities");
            AmenitiesDTO[] amenitiesDTOs = new AmenitiesDTO[amenities.size()];
            
            for (int i = 0; i < amenities.size(); i++) {
                JsonObject amenity = amenities.get(i).getAsJsonObject();
                amenitiesDTOs[i] = mapAmenity(amenity);
            }
            
            dto.setAmenities(amenitiesDTOs);
        }

        return dto;
    }

    private AmenitiesDTO mapAmenity(JsonObject amenity) {
        AmenitiesDTO dto = new AmenitiesDTO();
        dto.setDescription(amenity.get("description").getAsString());
        dto.setIsChargeable(amenity.get("isChargeable").getAsBoolean());
        return dto;
    }

    private AirportTravelingsInfoDTO mapAirportInfo(JsonObject airportInfo) {
        AirportTravelingsInfoDTO dto = new AirportTravelingsInfoDTO();
        dto.setAirlineCode(airportInfo.get("iataCode").getAsString());
        
        // Terminal might be optional in some responses
        if (airportInfo.has("terminal") && !airportInfo.get("terminal").isJsonNull()) {
            dto.setTerminal(airportInfo.get("terminal").getAsString());
        }
        
        dto.setDateTime(airportInfo.get("at").getAsString());
        return dto;
    }
}