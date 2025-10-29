package com.example.flightsapp.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class DurationFormatterTest {

    @Test
    void parseDuration_hoursAndMinutes() {
        Duration d = DurationFormatter.parseDuration("PT2H30M");
        assertNotNull(d);
        assertEquals(2, d.toHours());
        assertEquals(30, d.toMinutesPart());
    }

    @Test
    void parseDuration_hoursOnly() {
        Duration d = DurationFormatter.parseDuration("PT1H");
        assertNotNull(d);
        assertEquals(1, d.toHours());
        assertEquals(0, d.toMinutesPart());
    }

    @Test
    void parseDuration_minutesOnly() {
        Duration d = DurationFormatter.parseDuration("PT45M");
        assertNotNull(d);
        assertEquals(0, d.toHours());
        assertEquals(45, d.toMinutesPart());
    }

    @Test
    void parseDuration_invalid() {
        assertNull(DurationFormatter.parseDuration(null));
        assertNull(DurationFormatter.parseDuration(""));
        assertNull(DurationFormatter.parseDuration("2H30M"));
        assertNull(DurationFormatter.parseDuration("P2DT3H"));
    }

    @Test
    void formatHuman_hoursAndMinutes() {
        Duration d = Duration.ofHours(2).plusMinutes(30);
        String s = DurationFormatter.formatHuman(d);
        assertEquals("2h 30m", s);
    }

    @Test
    void formatHuman_hoursOnly() {
        Duration d = Duration.ofHours(1);
        String s = DurationFormatter.formatHuman(d);
        assertEquals("1h", s);
    }

    @Test
    void formatHuman_minutesOnly() {
        Duration d = Duration.ofMinutes(45);
        String s = DurationFormatter.formatHuman(d);
        assertEquals("45m", s);
    }
}