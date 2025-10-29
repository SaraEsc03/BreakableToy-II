package com.example.flightsapp.client;

import java.io.IOException;

public interface TokenProvider {
    String getValidAccessToken() throws IOException, InterruptedException;
}
