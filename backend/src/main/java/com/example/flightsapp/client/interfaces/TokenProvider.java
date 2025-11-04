package com.example.flightsapp.client.interfaces;

import java.io.IOException;

public interface TokenProvider {
    String getValidAccessToken() throws IOException, InterruptedException;
}
