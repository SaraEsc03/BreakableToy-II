package com.example.flightsapp.dtos.output.flights;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceTotalsResponseDTO {
    private String currency;
    private String total; //total before fees
    private String base;
    private FeesResponseDTO[] fees;
    private String grandTotal; 
}
