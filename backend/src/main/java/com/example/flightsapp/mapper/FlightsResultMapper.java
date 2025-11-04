package com.example.flightsapp.mapper;

import com.example.flightsapp.dtos.output.flights.FlightsResultDTO;
import com.example.flightsapp.client.interfaces.AirlineDirectory;
import com.example.flightsapp.client.interfaces.AirportDirectory;
import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import com.example.flightsapp.dtos.output.flights.AirportTravelingsInfoDTO;
import com.example.flightsapp.dtos.output.flights.AmenitiesDTO;
import com.example.flightsapp.dtos.output.flights.FareDetailsDTO;
import com.example.flightsapp.dtos.output.flights.FeesResponseDTO;
import com.example.flightsapp.dtos.output.flights.FlightOfferResponseDTO;
import com.example.flightsapp.dtos.output.flights.ItineraryDTO;
import com.example.flightsapp.dtos.output.flights.PriceTotalsResponseDTO;
import com.example.flightsapp.dtos.output.flights.PriceTravelerDetailsDTO;
import com.example.flightsapp.dtos.output.flights.SegmentDTO;
import com.example.flightsapp.dtos.output.flights.TravelerPricingsResponseDTO;

import java.util.Map;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.flightsapp.utils.FlightTimeUtils;
import com.example.flightsapp.utils.DurationFormatter;

@Component
public class FlightsResultMapper {

    /**
     * Transforms internal Amadeus FlightOfferResponseDTO objects into the
     * frontend-friendly FlightsResultDTO contract.
     *
     * Responsibilities:
     * - Pick representative pricing info (first traveler pricing) and
     *   attach totals/currency.
     * - Resolve airport codes to human-readable names via
     *   AirportDirectory.getAirportInfoByCode(...) (which consults a
     *   local cache and falls back to the API).
     * - Produce a lightweight DTO shape optimized for the frontend.
     *
     * Notes:
     * - This mapper may perform many cache lookups; keep the airport cache
     *   warm to minimize network calls.
     */

    private final AirportDirectory airportDirectory;
    private final AirlineDirectory airlineDirectory;

    public FlightsResultMapper(AirportDirectory airportDirectory, AirlineDirectory airlineDirectory) {
        this.airportDirectory = airportDirectory;
        this.airlineDirectory = airlineDirectory;
    }

    public FlightsResultDTO toFlightsResult(List<FlightOfferResponseDTO> offers) {
        // Convert a list of offers into the FlightsResultDTO. Defensive about
        // null inputs to make controller code straightforward.
        FlightsResultDTO result = new FlightsResultDTO();
        List<FlightsResultDTO.FlightOffer> outOffers = new ArrayList<>();

        if (offers == null) {
            result.setFlightOffers(outOffers);
            return result;
        }

        for (FlightOfferResponseDTO src : offers) {
            FlightsResultDTO.FlightOffer fo = new FlightsResultDTO.FlightOffer();
            fo.setId(src.getId());

            // price totals -> map 1:1 into FlightsResultDTO.PriceTotals
            PriceTotalsResponseDTO priceTotals = src.getPriceTotals();
            if (priceTotals != null) {
                FlightsResultDTO.PriceTotals outPrice = new FlightsResultDTO.PriceTotals();
                outPrice.setCurrency(priceTotals.getCurrency());
                outPrice.setTotal(priceTotals.getTotal());
                outPrice.setBase(priceTotals.getBase());
                outPrice.setGrandTotal(priceTotals.getGrandTotal());

                // map fees array -> list
                if (priceTotals.getFees() != null) {
                    List<FlightsResultDTO.Fees> feesList = new ArrayList<>();
                    for (FeesResponseDTO f : priceTotals.getFees()) {
                        if (f == null) continue;
                        FlightsResultDTO.Fees outFee = new FlightsResultDTO.Fees();
                        outFee.setAmount(f.getAmount());
                        outFee.setType(f.getType());
                        feesList.add(outFee);
                    }
                    outPrice.setFees(feesList);
                }

                fo.setPriceTotals(outPrice);
            }

            // traveler pricings -> map each traveler pricing with nested details
            if (src.getTravelerPricings() != null) {
                List<FlightsResultDTO.TravelerPricings> tpList = new ArrayList<>();
                for (TravelerPricingsResponseDTO tpSrc : src.getTravelerPricings()) {
                    if (tpSrc == null) continue;
                    FlightsResultDTO.TravelerPricings tpOut = new FlightsResultDTO.TravelerPricings();
                    tpOut.setTravelerId(tpSrc.getTravelerId());

                    // price details
                    PriceTravelerDetailsDTO ptd = tpSrc.getPriceDetails();
                    if (ptd != null) {
                        FlightsResultDTO.PriceTravelerDetails outPtd = new FlightsResultDTO.PriceTravelerDetails();
                        outPtd.setCurrency(ptd.getCurrency());
                        outPtd.setTotal(ptd.getTotal());
                        outPtd.setBase(ptd.getBase());
                        tpOut.setPriceTravelerDetails(outPtd);
                    }

                    // fare details by segment: map ALL fare details so the frontend
                    // can show per-segment cabins/classes/amenities.
                    FareDetailsDTO[] fares = tpSrc.getFareDetailsBySegment();
                    if (fares != null && fares.length > 0) {
                        List<FlightsResultDTO.FareDetails> outFares = new ArrayList<>();
                        for (FareDetailsDTO fd : fares) {
                            if (fd == null) continue;
                            FlightsResultDTO.FareDetails outFare = new FlightsResultDTO.FareDetails();
                            outFare.setSegmentId(fd.getSegmentId());
                            outFare.setCabin(fd.getCabin());
                            outFare.setClassTrip(fd.getClassTrip());

                            if (fd.getAmenities() != null) {
                                List<FlightsResultDTO.Amenities> amList = new ArrayList<>();
                                for (AmenitiesDTO a : fd.getAmenities()) {
                                    if (a == null) continue;
                                    FlightsResultDTO.Amenities outAmenity = new FlightsResultDTO.Amenities();
                                    outAmenity.setDescription(a.getDescription());
                                    outAmenity.setIsChargeable(a.getIsChargeable());
                                    amList.add(outAmenity);
                                }
                                outFare.setAmenities(amList);
                            }

                            outFares.add(outFare);
                        }
                        tpOut.setFareDetailsBySegment(outFares);
                    }

                    tpList.add(tpOut);
                }
                fo.setTravelerPricings(tpList);
            }

            // itineraries
            List<FlightsResultDTO.Itinerary> itinerariesOut = new ArrayList<>();
            if (src.getItineraries() != null) {
                for (ItineraryDTO iti : src.getItineraries()) {
                    FlightsResultDTO.Itinerary outIti = new FlightsResultDTO.Itinerary();
                    // Convert itinerary total duration (ISO format) to human-readable
                    outIti.setTotalDuration(DurationFormatter.formatHuman(iti.getTotalDuration()));

                    List<FlightsResultDTO.Segment> segOutList = new ArrayList<>();
                    // Initialize first departure and last arrival as null
                    String firstDeparture = null;
                    String lastArrival = null;
                    
                    if (iti.getSegments() != null) {
                        SegmentDTO[] segments = iti.getSegments();
                        // Get first departure from first segment
                        if (segments.length > 0) {
                            firstDeparture = segments[0].getDeparture().getDateTime();
                            // Get last arrival from last segment
                            lastArrival = segments[segments.length - 1].getArrival().getDateTime();
                        }
                        
                        for (SegmentDTO s : iti.getSegments()) {
                            FlightsResultDTO.Segment seg = new FlightsResultDTO.Segment();
                            seg.setId(s.getId());

                            // departure
                            AirportTravelingsInfoDTO dep = s.getDeparture();
                            if (dep != null) {
                                String code = dep.getAirlineCode();
                                FlightsResultDTO.AirportInfo ai = airportDirectory.getAirportInfoByCode(code);
                                seg.setDepartureAirport(ai);
                                seg.setDepartureDateTime(dep.getDateTime());
                            }

                            // arrival
                            AirportTravelingsInfoDTO arr = s.getArrival();
                            if (arr != null) {
                                String code = arr.getAirlineCode();
                                FlightsResultDTO.AirportInfo ai = airportDirectory.getAirportInfoByCode(code);
                                seg.setArrivalAirport(ai);
                                seg.setArrivalDateTime(arr.getDateTime());
                            }

                            seg.setFlightNumber(s.getNumber());
                            seg.setAircraftType(s.getAircraft());
                            // Convert segment duration (ISO format) to human-readable
                            seg.setDuration(DurationFormatter.formatHuman(s.getDuration()));

                            // airline info with names from lookup
                            FlightsResultDTO.AirlineInfo airline = new FlightsResultDTO.AirlineInfo();
                            airline.setCode(s.getCarrierCode());
                            airline.setName("Unknown Airline"); // Will be updated in second pass
                            seg.setAirline(airline);

                            if (s.getOperating() != null) {
                                FlightsResultDTO.AirlineInfo operating = new FlightsResultDTO.AirlineInfo();
                                operating.setCode(s.getOperating());
                                operating.setName("Unknown Airline"); // Will be updated in second pass
                                seg.setOperatingAirline(operating);
                            }

                            segOutList.add(seg);
                        }
                    }

                    outIti.setSegments(segOutList);
                    outIti.setInitialDepartureDateTime(firstDeparture);
                    outIti.setFinalArrivalDateTime(lastArrival);
                    
                    // Calculate and set stop times if there are multiple segments
                    if (iti.getSegments() != null && iti.getSegments().length > 1) {
                        List<FlightsResultDTO.StopInfo> stopTimes = FlightTimeUtils.calculateStopTimes(Arrays.asList(iti.getSegments()));
                        // Convert stop durations to human-readable and attach
                        if (stopTimes != null) {
                            for (FlightsResultDTO.StopInfo stopInfo : stopTimes) {
                                // convert ISO duration to human form
                                stopInfo.setDuration(DurationFormatter.formatHuman(stopInfo.getDuration()));
                                if (stopInfo.getAirport() != null && stopInfo.getAirport().getCode() != null) {
                                    FlightsResultDTO.AirportInfo airportInfo = airportDirectory.getAirportInfoByCode(
                                        stopInfo.getAirport().getCode()
                                    );
                                    stopInfo.setAirport(airportInfo);
                                }
                            }
                            outIti.setStopTimes(stopTimes);
                        }
                    }
                    
                    itinerariesOut.add(outIti);
                }
            }

            fo.setItineraries(itinerariesOut);
            outOffers.add(fo);
        }

        result.setFlightOffers(outOffers);
        
        // Get all airline details in one API call and update names
        Map<String, AirlineDetailsDTO> airlineDetails = airlineDirectory.getAirlinesForFlight(result);
        
        // Update airline names in all segments
        for (FlightsResultDTO.FlightOffer offer : result.getFlightOffers()) {
            for (FlightsResultDTO.Itinerary itinerary : offer.getItineraries()) {
                for (FlightsResultDTO.Segment segment : itinerary.getSegments()) {
                    // Update main airline name
                    if (segment.getAirline() != null && segment.getAirline().getCode() != null) {
                        String code = segment.getAirline().getCode();
                        AirlineDetailsDTO details = airlineDetails.get(code);
                        if (details != null) {
                            segment.getAirline().setName(details.getBusinessName());
                        }
                    }
                    
                    // Update operating airline name if present
                    if (segment.getOperatingAirline() != null && segment.getOperatingAirline().getCode() != null) {
                        String code = segment.getOperatingAirline().getCode();
                        AirlineDetailsDTO details = airlineDetails.get(code);
                        if (details != null) {
                            segment.getOperatingAirline().setName(details.getBusinessName());
                        }
                    }
                }
            }
        }
        
        return result;
    }
}
