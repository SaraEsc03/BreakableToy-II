package com.example.flightsapp.utils;

import com.example.flightsapp.dtos.output.flights.FlightsResultDTO;
import com.example.flightsapp.dtos.output.flights.SegmentDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for time-related calculations in flight itineraries.
 */
public class FlightTimeUtils {

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ISO_DATE_TIME,      // For "2025-12-23T23:37:00Z" format
        DateTimeFormatter.ISO_LOCAL_DATE_TIME // For "2025-12-23T23:37:00" format
    };

    /**
     * Calculates stop times between segments in an itinerary.
     * For each connection, it calculates the duration between the arrival of one segment
     * and the departure of the next segment.
     *
     * @param segments List of flight segments in chronological order
     * @return List of StopInfo objects containing connection details
     */
    public static List<FlightsResultDTO.StopInfo> calculateStopTimes(List<SegmentDTO> segments) {
        if (segments == null || segments.size() <= 1) {
            return null; // No stops for single-segment flights
        }
        
        // Debug logging
        System.out.println("Calculating stop times for " + segments.size() + " segments");

        List<FlightsResultDTO.StopInfo> stopTimes = new ArrayList<>();

        // Calculate duration for each connection between segments
        for (int i = 0; i < segments.size() - 1; i++) {
            SegmentDTO currentSegment = segments.get(i);
            SegmentDTO nextSegment = segments.get(i + 1);

            String arrivalTimeStr = currentSegment.getArrival().getDateTime();
            String departureTimeStr = nextSegment.getDeparture().getDateTime();
            
            // Debug logging
            System.out.println("Processing stop between segments:");
            System.out.println("  Arrival time (raw): " + arrivalTimeStr);
            System.out.println("  Departure time (raw): " + departureTimeStr);
            
            // Get arrival time of current segment
            LocalDateTime arrivalTime = parseDateTime(arrivalTimeStr);

            // Get departure time of next segment
            LocalDateTime departureTime = parseDateTime(departureTimeStr);

            // Calculate duration between flights
            Duration stopDuration = Duration.between(arrivalTime, departureTime);

            // Create StopInfo with airport details and formatted duration
                FlightsResultDTO.StopInfo stopInfo = new FlightsResultDTO.StopInfo(
                    new FlightsResultDTO.AirportInfo(
                        currentSegment.getArrival().getAirlineCode(),
                        null // Name will be populated by the mapper using airport service
                    ),
                    DurationFormatter.formatDuration(stopDuration)
                );

            stopTimes.add(stopInfo);
        }

        return stopTimes;
    }

    /**
     * Parse a datetime string using multiple possible formats
     * @param dateTime The datetime string to parse
     * @return LocalDateTime instance
     * @throws DateTimeParseException if the string cannot be parsed with any format
     */
    private static LocalDateTime parseDateTime(String dateTime) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTime, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
                continue;
            }
        }
        // If we get here, no formatter worked
        throw new DateTimeParseException("Could not parse datetime with any known format: " + dateTime, dateTime, 0);
    }

}