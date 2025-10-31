package com.example.flightsapp;

import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = FlightsAppApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class AirportAutocompleteIntegrationTest {

    private static MockWebServer mockWebServer;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (mockWebServer != null) mockWebServer.shutdown();
    }

    // Register MockWebServer endpoints as Amadeus API properties so the
    // client calls the enqueued mock responses.
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("amadeus.api.token-url", () -> mockWebServer.url("/v1/security/oauth2/token").toString());
        registry.add("amadeus.api.flight-search-url", () -> mockWebServer.url("/v2/shopping/flight-offers").toString());
        registry.add("amadeus.api.api-base-url", () -> mockWebServer.url("").toString());
    }

    @Test
    void airportsEndpoint_returnsSuggestions() {
        // Simulate token and airport response
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"access_token\":\"mock-token\", \"expires_in\":3600}"));

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"data\":[{\"name\":\"Munich Airport\",\"iataCode\":\"MUC\"}]}"));

        // Call the /api/v1/airports/search endpoint
        String url = "http://localhost:" + port + "/api/v1/airports/search?q=munich&limit=5";
        AirportDetailsDTO[] res = restTemplate.getForObject(url, AirportDetailsDTO[].class);

        // Check the results
        assertThat(res).isNotNull();
        assertThat(res.length).isGreaterThan(0);
        assertThat(res[0].getAirportCode()).isEqualTo("MUC");
    }
}
