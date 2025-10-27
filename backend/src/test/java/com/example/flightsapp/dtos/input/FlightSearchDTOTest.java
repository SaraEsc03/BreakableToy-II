package com.example.flightsapp.dtos.input;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FlightSearchDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll

    // ARRANGE
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void whenAllRequiredFieldsPresent_thenNoValidationErrors() {
        // ARRANGE
        FlightSearchDTO dto = new FlightSearchDTO();
        dto.setOrigin("LAX");
        dto.setDestination("JFK");
        dto.setDepartureDate("2025-12-01");
        // leave optional fields null

        // ACT
        Set<ConstraintViolation<FlightSearchDTO>> violations = validator.validate(dto);

        //ASSERT
        assertThat(violations).isEmpty();
    }

    @Test
    void whenRequiredFieldsMissing_thenValidationErrors() {
        // ARRANGE
        FlightSearchDTO dto = new FlightSearchDTO();
        // all fields blank

        // ACT
        Set<ConstraintViolation<FlightSearchDTO>> violations = validator.validate(dto);
        // Expect at least 3 violations: origin, destination, departureDate
        
        // ASSERT
        assertThat(violations).isNotEmpty();
        assertThat(violations.size()).isGreaterThanOrEqualTo(3);

        // Check that the violation property paths include the required fields
        boolean hasOrigin = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("origin"));
        boolean hasDestination = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("destination"));
        boolean hasDepartureDate = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("departureDate"));

        assertThat(hasOrigin).isTrue();
        assertThat(hasDestination).isTrue();
        assertThat(hasDepartureDate).isTrue();
    }

    @Test
    void defaultAdultsValue_shouldBeOne() {
        // ARRANGE
        FlightSearchDTO dto = new FlightSearchDTO();
        // Lombok-generated no-args constructor should leave adults with default value 1
        // ACT & ASSERT
        assertThat(dto.getAdults()).isEqualTo(1);

        // When we validate with required fields set, no violations expected for adults
        dto.setOrigin("SYD");
        dto.setDestination("BKK");
        dto.setDepartureDate("2025-12-02");
        Set<ConstraintViolation<FlightSearchDTO>> violations = validator.validate(dto);
        
        //ASSERT
        assertThat(violations).isEmpty();
    }
}