package com.example.flightsapp.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FareDetailsDTO {
    private String segmentId; //references the id of the segment to which the fare applies
    private String cabin; 
    private String classTrip; //map to "class"
    private AmenitiesDTO[] amenities;
}
