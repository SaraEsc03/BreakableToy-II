package com.example.flightsapp.utils;

import java.time.Duration;

/**
 * Utility class for formatting durations in ISO-8601 format.
 */


public class DurationFormatter {
    /**
     * Formats a Duration object into ISO-8601 duration format.
     * Examples:
     * - 2 hours and 30 minutes -> "PT2H30M"
     * - 1 hour -> "PT1H"
     * - 45 minutes -> "PT45M"
     *
     * @param duration The duration to format
     * @return Formatted string in ISO-8601 duration format
     */
    public static String formatDuration(Duration duration) {
        if (duration == null) {
            return null;
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        
        StringBuilder formatted = new StringBuilder("PT");
        if (hours > 0) {
            formatted.append(hours).append("H");
        }
        if (minutes > 0 || hours == 0) {  // Include minutes if there are no hours
            formatted.append(minutes).append("M");
        }
        
        return formatted.toString();
    }


    /**
     * Formats a Duration object into human-readable text.
     * Examples:
     * - 2 hours and 30 minutes -> "2h 30m"
     * - 1 hour -> "1h"
     * - 45 minutes -> "45m"
     *
     * @param duration The duration to format
     * @return Formatted string in natural language
     */
    public static String formatHuman(Duration duration) {
        if (duration == null) {
            return null;
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        
        StringBuilder formatted = new StringBuilder();
        if (hours > 0) {
            formatted.append(hours).append("h");
        }
        if (minutes > 0 || hours == 0) {  // Include minutes if there are no hours
            if (hours > 0) {
                formatted.append(" ");
            }
            formatted.append(minutes).append("m");
        }
        
        return formatted.toString();
    }



 /**
     * Parses an ISO-8601 duration string into a Duration object.
     * Supports hour (H) and minute (M) units.
     * Examples:
     * - "PT2H30M" -> 2 hours and 30 minutes
     * - "PT1H" -> 1 hour
     * - "PT45M" -> 45 minutes
     *
     * @param durationStr The ISO-8601 duration string to parse
     * @return Duration object, or null if the string is invalid
     */
    public static Duration parseDuration(String durationStr) {
        if (durationStr == null || !durationStr.startsWith("PT")) {
            return null;
        }

        try {
            long hours = 0;
            long minutes = 0;

            String duration = durationStr.substring(2); // Remove "PT"
            int hIndex = duration.indexOf("H");
            int mIndex = duration.indexOf("M");

            if (hIndex > 0) {
                hours = Long.parseLong(duration.substring(0, hIndex));
                if (mIndex > 0) {
                    minutes = Long.parseLong(duration.substring(hIndex + 1, mIndex));
                }
            } else if (mIndex > 0) {
                minutes = Long.parseLong(duration.substring(0, mIndex));
            }

            return Duration.ofHours(hours).plusMinutes(minutes);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Parse an ISO-8601 duration string and return a human-readable version.
     * If the input is already null or cannot be parsed, returns null.
     *
     * @param isoDuration ISO-8601 duration string like "PT2H30M"
     * @return human-readable string like "2h 30m" or null if unparsable
     */
    public static String formatHuman(String isoDuration) {
        if (isoDuration == null) return null;
        Duration d = parseDuration(isoDuration);
        return d == null ? null : formatHuman(d);
    }
}