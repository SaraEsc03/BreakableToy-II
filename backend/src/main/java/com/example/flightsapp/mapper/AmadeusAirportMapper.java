package com.example.flightsapp.mapper;

import com.example.flightsapp.dtos.output.auxiliars.AirportDetailsDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AmadeusAirportMapper {
    /**
     * Mapper for Amadeus airport/location responses.
     *
     * Converts the Amadeus reference-data/locations JSON payload into a
     * list of AirportDetailsDTO objects (name + iata code). The mapper is
     * intentionally small and defensive: it tolerates missing fields and
     * returns an empty list when the response contains no data.
     *
     * @param json raw JSON response from Amadeus reference-data API
     * @return list of AirportDetailsDTO (possibly empty)
     */
    public List<AirportDetailsDTO> parseAirportList(String json) {
        List<AirportDetailsDTO> list = new ArrayList<>();
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        if (!root.has("data")) return list;

        JsonArray dataArray = root.getAsJsonArray("data");
        for (JsonElement el : dataArray) {
            JsonObject obj = el.getAsJsonObject();
            String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown";
            String code = obj.has("iataCode") ? obj.get("iataCode").getAsString() : "UNK";

            list.add(new AirportDetailsDTO(name, code));
        }
        return list;
    }
}
