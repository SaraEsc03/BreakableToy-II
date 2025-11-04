package com.example.flightsapp.client;

import com.example.flightsapp.client.interfaces.TokenProvider;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
@Slf4j
public class AmadeusAuthService implements TokenProvider {

    private final HttpClient httpClient;
    private final AmadeusApiProperties properties;

    private volatile String accessToken;
    private volatile Instant tokenExpiry;

    public AmadeusAuthService(HttpClient httpClient, AmadeusApiProperties properties) {
        this.httpClient = httpClient;
        this.properties = properties;
    }

    private void authenticate() throws IOException, InterruptedException {
        String form = "grant_type=client_credentials"
                + "&client_id=" + URLEncoder.encode(properties.getClientId(), StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(properties.getClientSecret(), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getTokenUrl()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to authenticate with Amadeus API: " + response.body());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        this.accessToken = json.get("access_token").getAsString();
        int expiresIn = json.get("expires_in").getAsInt();
        this.tokenExpiry = Instant.now().plusSeconds(expiresIn - 60);
    }

    @Override
    public String getValidAccessToken() throws IOException, InterruptedException {
        if (accessToken == null || tokenExpiry == null || Instant.now().isAfter(tokenExpiry)) {
            synchronized (this) {
                if (accessToken == null || tokenExpiry == null || Instant.now().isAfter(tokenExpiry)) {
                    authenticate();
                    log.info("Refreshed Amadeus access token; expires at {}", tokenExpiry);
                }
            }
        }
        return accessToken;
    }
}
