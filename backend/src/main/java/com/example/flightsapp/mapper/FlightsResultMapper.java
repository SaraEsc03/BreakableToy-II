package com.example.flightsapp.mapper;

import com.example.flightsapp.FlightsResultDTO;
import com.example.flightsapp.client.AmadeusApiClientService;
import com.example.flightsapp.dtos.output.flights.AirportTravelingsInfoDTO;
import com.example.flightsapp.dtos.output.flights.FlightOfferResponseDTO;
import com.example.flightsapp.dtos.output.flights.ItineraryDTO;
import com.example.flightsapp.dtos.output.flights.SegmentDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
     *   AmadeusApiClientService.getAirportInfoByCode(...) (which consults a
     *   local cache and falls back to the API).
     * - Produce a lightweight DTO shape optimized for the frontend.
     *
     * Notes:
     * - This mapper may perform many cache lookups; keep the airport cache
     *   warm to minimize network calls.
     */

    private final AmadeusApiClientService airportService;

    public FlightsResultMapper(AmadeusApiClientService airportService) {
        this.airportService = airportService;
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

            // price totals
            if (src.getPriceTotals() != null) {
                fo.setTotalPrice(src.getPriceTotals().getGrandTotal());
                fo.setCurrency(src.getPriceTotals().getCurrency());
            }

            // traveler pricing (take first as representative)
            if (src.getTravelerPricings() != null && src.getTravelerPricings().length > 0) {
                try {
                    fo.setPricePerTraveler(src.getTravelerPricings()[0].getPriceDetails().getTotal());
                } catch (Exception ignored) {
                }
            }

            // itineraries
            List<FlightsResultDTO.Itinerary> itinerariesOut = new ArrayList<>();
            if (src.getItineraries() != null) {
                for (ItineraryDTO iti : src.getItineraries()) {
                    FlightsResultDTO.Itinerary outIti = new FlightsResultDTO.Itinerary();
                    outIti.setTotalDuration(iti.getTotalDuration());

                    List<FlightsResultDTO.Segment> segOutList = new ArrayList<>();
                    if (iti.getSegments() != null) {
                        for (SegmentDTO s : iti.getSegments()) {
                            FlightsResultDTO.Segment seg = new FlightsResultDTO.Segment();
                            seg.setId(s.getId());

                            // departure
                            AirportTravelingsInfoDTO dep = s.getDeparture();
                            if (dep != null) {
                                String code = dep.getAirlineCode();
                                FlightsResultDTO.AirportInfo ai = airportService.getAirportInfoByCode(code);
                                seg.setDepartureAirport(ai);
                                seg.setDepartureDateTime(dep.getDateTime());
                            }

                            // arrival
                            AirportTravelingsInfoDTO arr = s.getArrival();
                            if (arr != null) {
                                String code = arr.getAirlineCode();
                                FlightsResultDTO.AirportInfo ai = airportService.getAirportInfoByCode(code);
                                seg.setArrivalAirport(ai);
                                seg.setArrivalDateTime(arr.getDateTime());
                            }

                            seg.setFlightNumber(s.getNumber());
                            seg.setAircraftType(s.getAircraft());
                            seg.setDuration(s.getDuration());

                            // airline info (code only; name not available here)
                            FlightsResultDTO.AirlineInfo airline = new FlightsResultDTO.AirlineInfo();
                            airline.setCode(s.getCarrierCode());
                            seg.setAirline(airline);

                            FlightsResultDTO.AirlineInfo operating = new FlightsResultDTO.AirlineInfo();
                            operating.setCode(s.getOperating());
                            seg.setOperatingAirline(operating);

                            segOutList.add(seg);
                        }
                    }

                    outIti.setSegments(segOutList);
                    itinerariesOut.add(outIti);
                }
            }

            fo.setItineraries(itinerariesOut);
            outOffers.add(fo);
        }

        result.setFlightOffers(outOffers);
        return result;
    }
}
