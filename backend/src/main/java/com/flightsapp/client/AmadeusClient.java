package com.flightsapp.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConfigurationProperties(prefix = "amadeus.api")
@Getter
@Setter
public class AmadeusClient {
   private String clientId;
   private String clientSecret;
   private String tokenUrl;
   private String flightSearchUrl;
}