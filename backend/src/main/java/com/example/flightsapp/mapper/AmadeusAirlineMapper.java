package com.example.flightsapp.mapper;

import com.example.flightsapp.dtos.output.auxiliars.AirlineDetailsDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapper for Amadeus airline responses.
 *
 * Converts the Amadeus reference-data/airlines JSON payload into
 * AirlineDetailsDTO objects (businessName + airlineCode). The mapper is
 * intentionally defensive: it tolerates missing fields and returns null
 * when the response contains no data.
 */
@Component
public class AmadeusAirlineMapper {
    
    public AirlineDetailsDTO parseAirlineDetails(String json) {
        if (json == null || json.isEmpty() || json.equals("{}")) {
            return null;
        }

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("data")) return null;

            JsonObject data = root.getAsJsonObject("data");
            String iataCode = data.has("iataCode") ? data.get("iataCode").getAsString() : null;
            String businessName = data.has("businessName") ? data.get("businessName").getAsString() : 
                                (data.has("commonName") ? data.get("commonName").getAsString() : "Unknown Airline");

            return new AirlineDetailsDTO(iataCode, businessName);
        } catch (Exception e) {
            return null;
        }
    }

    public List<AirlineDetailsDTO> parseAirlineDetailsList(String json) {
        if (json == null || json.isEmpty() || json.equals("{}")) {
            return Collections.emptyList();
        }

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("data")) return Collections.emptyList();

            JsonArray dataArray = root.getAsJsonArray("data");
            List<AirlineDetailsDTO> airlines = new ArrayList<>();

            for (JsonElement element : dataArray) {
                JsonObject airlineData = element.getAsJsonObject();
                String iataCode = airlineData.has("iataCode") ? airlineData.get("iataCode").getAsString() : null;
                String businessName = airlineData.has("businessName") ? airlineData.get("businessName").getAsString() : 
                                    (airlineData.has("commonName") ? airlineData.get("commonName").getAsString() : "Unknown Airline");

                airlines.add(new AirlineDetailsDTO(iataCode, businessName));
            }

            return airlines;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}