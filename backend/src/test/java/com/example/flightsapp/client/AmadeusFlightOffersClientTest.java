package com.example.flightsapp.client;

import com.example.flightsapp.client.interfaces.TokenProvider;
import com.example.flightsapp.dtos.input.FlightSearchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AmadeusFlightOffersClientTest {

    private HttpClient mockHttpClient;
    private TokenProvider mockTokenProvider;
    private AmadeusApiProperties properties;
    private AmadeusFlightOffersClient client;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockTokenProvider = mock(TokenProvider.class);
        properties = new AmadeusApiProperties();
        properties.setFlightSearchUrl("https://test.api.amadeus.com/v2/shopping/flight-offers");
        
        client = new AmadeusFlightOffersClient(mockHttpClient, mockTokenProvider, properties);
    }

    @Test
    void searchFlights_shouldBuildCorrectRequest() throws Exception {
        // Arrange
        when(mockTokenProvider.getValidAccessToken()).thenReturn("test-token");
        
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"data\":[]}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        FlightSearchDTO request = new FlightSearchDTO();
        request.setOrigin("LAX");
        request.setDestination("JFK");
        request.setDepartureDate("2025-12-25");
        request.setAdults(2);
        request.setNonStop(true);

        // Act
        String result = client.searchFlights(request);

        // Assert
        assertThat(result).isEqualTo("{\"data\":[]}");
        
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.uri().toString()).contains("originLocationCode=LAX");
        assertThat(capturedRequest.uri().toString()).contains("destinationLocationCode=JFK");
        assertThat(capturedRequest.uri().toString()).contains("adults=2");
        assertThat(capturedRequest.uri().toString()).contains("nonStop=true");
        assertThat(capturedRequest.headers().firstValue("Authorization").get()).isEqualTo("Bearer test-token");
    }

    @Test
    void searchFlights_shouldHandleExceptions() throws Exception {
        // Arrange
        when(mockTokenProvider.getValidAccessToken()).thenThrow(new java.io.IOException("Token error"));

        FlightSearchDTO request = new FlightSearchDTO();
        request.setOrigin("LAX");
        request.setDestination("JFK");
        request.setDepartureDate("2025-12-25");

        // Act
        String result = client.searchFlights(request);

        // Assert
        assertThat(result).isEqualTo("{}");
    }
}
