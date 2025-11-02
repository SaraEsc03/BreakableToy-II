package com.example.flightsapp.client;

import com.example.flightsapp.client.interfaces.TokenProvider;
import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;
import com.example.flightsapp.dtos.output.flights.FlightsResultDTO.AirportInfo;
import com.example.flightsapp.mapper.AmadeusAirportMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AmadeusAirportServiceTest {

    private HttpClient mockHttpClient;
    private TokenProvider mockTokenProvider;
    private AmadeusApiProperties properties;
    private AmadeusAirportMapper mockMapper;
    private AmadeusAirportService service;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockTokenProvider = mock(TokenProvider.class);
        mockMapper = mock(AmadeusAirportMapper.class);
        properties = new AmadeusApiProperties();
        properties.setApiBaseUrl("https://test.api.amadeus.com");
        
        service = new AmadeusAirportService(mockHttpClient, mockTokenProvider, properties, mockMapper);
    }

    @SuppressWarnings("unchecked")
    @Test
    void searchAirportsForAutocomplete_shouldCallApiAndCache() throws Exception {
        // Arrange
        when(mockTokenProvider.getValidAccessToken()).thenReturn("test-token");
        
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"data\":[]}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        AirportDetailsDTO dto = new AirportDetailsDTO("Los Angeles Intl", "LAX");
        when(mockMapper.parseAirportList(anyString())).thenReturn(List.of(dto));

        // Act
        List<AirportDetailsDTO> results = service.searchAirportsForAutocomplete("los", 10);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAirportCode()).isEqualTo("LAX");
        
        // Second call should use cache
        List<AirportDetailsDTO> cachedResults = service.searchAirportsForAutocomplete("los", 10);
        assertThat(cachedResults).hasSize(1);
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAirportInfoByCode_shouldResolveCode() throws Exception {
        // Arrange
        when(mockTokenProvider.getValidAccessToken()).thenReturn("test-token");
        
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"data\":[]}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        AirportDetailsDTO dto = new AirportDetailsDTO("Los Angeles Intl", "LAX");
        when(mockMapper.parseAirportList(anyString())).thenReturn(List.of(dto));

        // Act
        AirportInfo info = service.getAirportInfoByCode("LAX");

        // Assert
        assertThat(info.getCode()).isEqualTo("LAX");
        assertThat(info.getName()).isEqualTo("Los Angeles Intl");
    }

    @Test
    void getAirportInfoByCode_shouldReturnUnknownForNull() {
        // Act
        AirportInfo info = service.getAirportInfoByCode(null);

        // Assert
        assertThat(info.getName()).isEqualTo("Unknown Airport");
    }
}
