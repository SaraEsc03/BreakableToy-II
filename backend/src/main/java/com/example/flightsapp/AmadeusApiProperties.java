package com.example.flightsapp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConfigurationProperties(prefix = "amadeus.api")
@Getter
@Setter
public class AmadeusApiProperties {
   private String clientId;
   private String clientSecret;
   private String tokenUrl;
   private String flightSearchUrl;
}