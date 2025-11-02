package com.example.flightsapp.client;

import com.example.flightsapp.client.interfaces.TokenProvider;
import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import com.example.flightsapp.dtos.output.flights.FlightsResultDTO;
import com.example.flightsapp.mapper.AmadeusAirlineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AmadeusAirlineServiceTest {

    private HttpClient mockHttpClient;
    private TokenProvider mockTokenProvider;
    private AmadeusApiProperties properties;
    private AmadeusAirlineMapper mockMapper;
    private AmadeusAirlineService service;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockTokenProvider = mock(TokenProvider.class);
        mockMapper = mock(AmadeusAirlineMapper.class);
        properties = new AmadeusApiProperties();
        properties.setApiBaseUrl("https://test.api.amadeus.com");
        
        service = new AmadeusAirlineService(mockHttpClient, mockTokenProvider, properties, mockMapper);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAirlineDetails_shouldCallApi() throws Exception {
        // Arrange
        when(mockTokenProvider.getValidAccessToken()).thenReturn("test-token");
        
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"data\":{}}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Act
        String result = service.getAirlineDetails("AA", "UA");

        // Assert
        assertThat(result).isEqualTo("{\"data\":{}}");
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAirlinesForFlight_shouldExtractAndMapAirlines() throws Exception {
        // Arrange
        when(mockTokenProvider.getValidAccessToken()).thenReturn("test-token");
        
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"data\":[]}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        AirlineDetailsDTO aa = new AirlineDetailsDTO("AA", "American Airlines");
        when(mockMapper.parseAirlineDetailsList(anyString())).thenReturn(List.of(aa));

        FlightsResultDTO flightResult = new FlightsResultDTO();
        FlightsResultDTO.FlightOffer offer = new FlightsResultDTO.FlightOffer();
        FlightsResultDTO.Itinerary itinerary = new FlightsResultDTO.Itinerary();
        FlightsResultDTO.Segment segment = new FlightsResultDTO.Segment();
        
        FlightsResultDTO.AirlineInfo airline = new FlightsResultDTO.AirlineInfo();
        airline.setCode("AA");
        segment.setAirline(airline);
        
        itinerary.setSegments(List.of(segment));
        offer.setItineraries(List.of(itinerary));
        flightResult.setFlightOffers(List.of(offer));

        // Act
        Map<String, AirlineDetailsDTO> result = service.getAirlinesForFlight(flightResult);

        // Assert
        assertThat(result).containsKey("AA");
        assertThat(result.get("AA").getBusinessName()).isEqualTo("American Airlines");
    }
}
