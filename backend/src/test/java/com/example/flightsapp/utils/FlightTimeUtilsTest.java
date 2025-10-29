package com.example.flightsapp.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.flightsapp.FlightsResultDTO;
import com.example.flightsapp.dtos.output.flights.AirportTravelingsInfoDTO;
import com.example.flightsapp.dtos.output.flights.SegmentDTO;

class FlightTimeUtilsTest {

    @Test
    void testCalculateStopTimes_twoSegments() {
        // Given
        SegmentDTO s1 = createSegment("2025-10-29T10:00:00", "2025-10-29T12:00:00", "LHR", "CDG");
        SegmentDTO s2 = createSegment("2025-10-29T14:00:00", "2025-10-29T16:00:00", "CDG", "MAD");
        List<SegmentDTO> segments = Arrays.asList(s1, s2);

        // When
        List<FlightsResultDTO.StopInfo> stops = FlightTimeUtils.calculateStopTimes(segments);

        // Then
        assertNotNull(stops);
        assertEquals(1, stops.size());
        FlightsResultDTO.StopInfo stop = stops.get(0);
        // stop airport code is taken from arrival.airlineCode of the earlier segment
        assertEquals("CDG", stop.getAirport().getCode());
        assertEquals("PT2H", stop.getDuration()); // 12:00 -> 14:00
    }

    @Test
    void testCalculateStopTimes_multipleStops() {
        SegmentDTO s1 = createSegment("2025-10-29T08:00:00", "2025-10-29T10:00:00", "LHR", "CDG");
        SegmentDTO s2 = createSegment("2025-10-29T11:30:00", "2025-10-29T13:00:00", "CDG", "FRA");
        SegmentDTO s3 = createSegment("2025-10-29T14:00:00", "2025-10-29T16:00:00", "FRA", "MAD");
        List<SegmentDTO> segments = Arrays.asList(s1, s2, s3);

        List<FlightsResultDTO.StopInfo> stops = FlightTimeUtils.calculateStopTimes(segments);

        assertNotNull(stops);
        assertEquals(2, stops.size());

        // First stop between s1 arrival (10:00) and s2 departure (11:30) -> 1h30m
        assertEquals("CDG", stops.get(0).getAirport().getCode());
        assertEquals("PT1H30M", stops.get(0).getDuration());

        // Second stop between s2 arrival (13:00) and s3 departure (14:00) -> 1h
        assertEquals("FRA", stops.get(1).getAirport().getCode());
        assertEquals("PT1H", stops.get(1).getDuration());
    }

    @Test
    void testCalculateStopTimes_singleSegment() {
        SegmentDTO s = createSegment("2025-10-29T10:00:00", "2025-10-29T12:00:00", "LHR", "MAD");
        List<FlightsResultDTO.StopInfo> stops = FlightTimeUtils.calculateStopTimes(Arrays.asList(s));
        assertNull(stops);
    }

    @Test
    void testCalculateStopTimes_nullInput() {
        assertNull(FlightTimeUtils.calculateStopTimes(null));
    }

    private SegmentDTO createSegment(String depTime, String arrTime, String depCode, String arrCode) {
        SegmentDTO s = new SegmentDTO();

        AirportTravelingsInfoDTO dep = new AirportTravelingsInfoDTO();
        dep.setDateTime(depTime);
        dep.setAirlineCode(depCode);
        s.setDeparture(dep);

        AirportTravelingsInfoDTO arr = new AirportTravelingsInfoDTO();
        arr.setDateTime(arrTime);
        arr.setAirlineCode(arrCode);
        s.setArrival(arr);

        return s;
    }
}
