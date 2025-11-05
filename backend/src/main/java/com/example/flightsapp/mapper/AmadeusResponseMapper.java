package com.example.flightsapp.mapper;

import com.example.flightsapp.dtos.output.flights.AirportTravelingsInfoDTO;
import com.example.flightsapp.dtos.output.flights.AmenitiesDTO;
import com.example.flightsapp.dtos.output.flights.FareDetailsDTO;
import com.example.flightsapp.dtos.output.flights.FlightOfferResponseDTO;
import com.example.flightsapp.dtos.output.flights.ItineraryDTO;
import com.example.flightsapp.dtos.output.flights.PriceTotalsResponseDTO;
import com.example.flightsapp.dtos.output.flights.FeesResponseDTO;
import com.example.flightsapp.dtos.output.flights.PriceTravelerDetailsDTO;
import com.example.flightsapp.dtos.output.flights.SegmentDTO;
import com.example.flightsapp.dtos.output.flights.TravelerPricingsResponseDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AmadeusResponseMapper {

    /**
     * Mapper for Amadeus flight-offers JSON responses.
     *
     * Purpose: convert the raw Amadeus JSON structure into the project's
     * internal DTOs (located under dtos.output.flights). This class encapsulates
     * the parsing rules and defensive checks required when fields are optional
     * or missing in the third-party response.
     *
     * Notes:
     * - This mapper intentionally works with Gson JsonObjects to avoid creating
     *   intermediate generic Maps and to keep mapping logic explicit.
     * - The mapper does not perform network calls or validation of business
     *   rules; it only translates JSON -> DTOs.
     */

    public List<FlightOfferResponseDTO> mapFlightOffers(JsonObject amadeusResponse) {
        List<FlightOfferResponseDTO> flightOffers = new ArrayList<>();
        if (amadeusResponse == null) return flightOffers;

        // Be defensive: some Amadeus responses (or error cases) may not include
        // a top-level "data" array. Treat missing or non-array "data" as
        // an empty result set instead of throwing NullPointerException.
        JsonArray data = amadeusResponse.has("data") && amadeusResponse.get("data").isJsonArray()
                ? amadeusResponse.getAsJsonArray("data")
                : new JsonArray();

        for (JsonElement element : data) {
            JsonObject offer = element.getAsJsonObject();
            FlightOfferResponseDTO flightOffer = mapFlightOffer(offer);
            flightOffers.add(flightOffer);
        }

        return flightOffers;
    }

    private FlightOfferResponseDTO mapFlightOffer(JsonObject offer) {
        // Create and populate a FlightOfferResponseDTO from an Amadeus 'data' element
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
        // Map price totals (currency, total, base, grandTotal) into DTO
        PriceTotalsResponseDTO priceDTO = new PriceTotalsResponseDTO();
        priceDTO.setCurrency(price.get("currency").getAsString());
        priceDTO.setTotal(price.get("total").getAsString());
        priceDTO.setBase(price.get("base").getAsString());
        priceDTO.setGrandTotal(price.get("grandTotal").getAsString());
        // Map fees if present (some Amadeus responses include a fees array)
        if (price.has("fees") && price.get("fees").isJsonArray()) {
            JsonArray feesArr = price.getAsJsonArray("fees");
            FeesResponseDTO[] feesDto = new FeesResponseDTO[feesArr.size()];
            for (int i = 0; i < feesArr.size(); i++) {
                JsonObject fee = feesArr.get(i).getAsJsonObject();
                FeesResponseDTO f = new FeesResponseDTO();
                if (fee.has("amount") && !fee.get("amount").isJsonNull()) f.setAmount(fee.get("amount").getAsString());
                if (fee.has("type") && !fee.get("type").isJsonNull()) f.setType(fee.get("type").getAsString());
                feesDto[i] = f;
            }
            priceDTO.setFees(feesDto);
        }
        return priceDTO;
    }

    private ItineraryDTO mapItinerary(JsonObject itinerary) {
        // Map a single itinerary which may contain multiple flight segments
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
        // Map a flight segment. Handles optional fields like aircraft and
        // operating carrier gracefully.
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
        // Map traveler pricing details for a single traveler (id, type, price,
        // and fare details per segment). The caller may use the first traveler
        // pricing as a representative price per traveler.
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
        // Map price details specific to a traveler
        PriceTravelerDetailsDTO dto = new PriceTravelerDetailsDTO();
        dto.setCurrency(price.get("currency").getAsString());
        dto.setTotal(price.get("total").getAsString());
        dto.setBase(price.get("base").getAsString());
        return dto;
    }

    private FareDetailsDTO mapFareDetails(JsonObject fareDetail) {
        // Map fare details by segment including cabin and amenity list
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
        // Map a single amenity (description + whether it is chargeable)
        AmenitiesDTO dto = new AmenitiesDTO();
        dto.setDescription(amenity.get("description").getAsString());
        dto.setIsChargeable(amenity.get("isChargeable").getAsBoolean());
        return dto;
    }

    private AirportTravelingsInfoDTO mapAirportInfo(JsonObject airportInfo) {
        // Map the small airport/time block that appears inside a segment
        // (departure/arrival). Some properties like terminal may be optional
        // so we check for their presence and nullability.
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