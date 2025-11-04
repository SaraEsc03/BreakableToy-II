package com.example.flightsapp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AmadeusAuthServiceTest {

    private HttpClient mockHttpClient;
    private AmadeusApiProperties properties;
    private AmadeusAuthService authService;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        properties = new AmadeusApiProperties();
        properties.setClientId("test-client-id");
        properties.setClientSecret("test-client-secret");
        properties.setTokenUrl("https://test.api.amadeus.com/v1/security/oauth2/token");
        
        authService = new AmadeusAuthService(mockHttpClient, properties);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getValidAccessToken_shouldAuthenticateAndReturnToken() throws Exception {
        // Arrange
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"access_token\":\"test-token\",\"expires_in\":1799}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Act
        String token = authService.getValidAccessToken();

        // Assert
        assertThat(token).isEqualTo("test-token");
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getValidAccessToken_shouldReuseValidToken() throws Exception {
        // Arrange
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"access_token\":\"test-token\",\"expires_in\":1799}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Act
        String token1 = authService.getValidAccessToken();
        String token2 = authService.getValidAccessToken();

        // Assert
        assertThat(token1).isEqualTo("test-token");
        assertThat(token2).isEqualTo("test-token");
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
