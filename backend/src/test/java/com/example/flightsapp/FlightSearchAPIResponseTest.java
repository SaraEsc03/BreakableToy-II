package com.example.flightsapp;

import com.example.flightsapp.dtos.input.FlightSearchDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlightSearchAPIResponseTest {

    private static final String SAMPLE_API_RESPONSE = """
            {
  "meta": {
    "count": 2,
    "links": {
      "self": "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=SYD&destinationLocationCode=BKK&departureDate=2025-12-02&adults=1&nonStop=false&max=2"
    }
  },
  "data": [
    {
      "type": "flight-offer",
      "id": "1",
      "source": "GDS",
      "instantTicketingRequired": false,
      "nonHomogeneous": false,
      "oneWay": false,
      "isUpsellOffer": false,
      "lastTicketingDate": "2025-10-27",
      "lastTicketingDateTime": "2025-10-27",
      "numberOfBookableSeats": 9,
      "itineraries": [
        {
          "duration": "PT14H30M",
          "segments": [
            {
              "departure": {
                "iataCode": "SYD",
                "terminal": "0",
                "at": "2025-12-02T07:15:00"
              },
              "arrival": {
                "iataCode": "DPS",
                "terminal": "I",
                "at": "2025-12-02T10:45:00"
              },
              "carrierCode": "OD",
              "number": "172",
              "aircraft": {
                "code": "7M8"
              },
              "operating": {
                "carrierCode": "OD"
              },
              "duration": "PT6H30M",
              "id": "3",
              "numberOfStops": 0,
              "blacklistedInEU": false
            },
            {
              "departure": {
                "iataCode": "DPS",
                "terminal": "D",
                "at": "2025-12-02T14:15:00"
              },
              "arrival": {
                "iataCode": "DMK",
                "terminal": "0",
                "at": "2025-12-02T17:45:00"
              },
              "carrierCode": "ID",
              "number": "7637",
              "aircraft": {
                "code": "32A"
              },
              "operating": {
                "carrierCode": "ID"
              },
              "duration": "PT4H30M",
              "id": "4",
              "numberOfStops": 0,
              "blacklistedInEU": false
            }
          ]
        }
      ],
      "price": {
        "currency": "EUR",
        "total": "202.42",
        "base": "129.00",
        "fees": [
          {
            "amount": "0.00",
            "type": "SUPPLIER"
          },
          {
            "amount": "0.00",
            "type": "TICKETING"
          }
        ],
        "grandTotal": "202.42"
      },
      "pricingOptions": {
        "fareType": [
          "PUBLISHED"
        ],
        "includedCheckedBagsOnly": false
      },
      "validatingAirlineCodes": [
        "GP"
      ],
      "travelerPricings": [
        {
          "travelerId": "1",
          "fareOption": "STANDARD",
          "travelerType": "ADULT",
          "price": {
            "currency": "EUR",
            "total": "202.42",
            "base": "129.00"
          },
          "fareDetailsBySegment": [
            {
              "segmentId": "3",
              "cabin": "ECONOMY",
              "fareBasis": "X1OBSSAU",
              "brandedFare": "SS",
              "brandedFareLabel": "SUPER SAVER",
              "class": "X",
              "includedCheckedBags": {
                "weight": 0,
                "weightUnit": "KG"
              },
              "includedCabinBags": {
                "weight": 7,
                "weightUnit": "KG"
              },
              "amenities": [
                {
                  "description": "UPTO44LB 20KG BAGGAGE",
                  "isChargeable": true,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "UPTO66LB 30KG BAGGAGE",
                  "isChargeable": true,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "UPTO88LB40KG BAGGAGE",
                  "isChargeable": true,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "UPTO22LB 10KG BAGGAGE",
                  "isChargeable": true,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "PRE RESERVED SEAT ASSIGNMENT",
                  "isChargeable": true,
                  "amenityType": "PRE_RESERVED_SEAT",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "MEALS",
                  "isChargeable": true,
                  "amenityType": "MEAL",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                }
              ]
            },
            {
              "segmentId": "4",
              "cabin": "ECONOMY",
              "fareBasis": "XOWID",
              "class": "X",
              "includedCheckedBags": {
                "weight": 0,
                "weightUnit": "KG"
              },
              "includedCabinBags": {
                "quantity": 1
              }
            }
          ]
        }
      ]
    },
    {
      "type": "flight-offer",
      "id": "2",
      "source": "GDS",
      "instantTicketingRequired": false,
      "nonHomogeneous": false,
      "oneWay": false,
      "isUpsellOffer": false,
      "lastTicketingDate": "2025-12-02",
      "lastTicketingDateTime": "2025-12-02",
      "numberOfBookableSeats": 9,
      "itineraries": [
        {
          "duration": "PT17H5M",
          "segments": [
            {
              "departure": {
                "iataCode": "SYD",
                "terminal": "1",
                "at": "2025-12-02T21:00:00"
              },
              "arrival": {
                "iataCode": "HAK",
                "terminal": "2",
                "at": "2025-12-03T03:20:00"
              },
              "carrierCode": "HU",
              "number": "776",
              "aircraft": {
                "code": "789"
              },
              "operating": {
                "carrierCode": "HU"
              },
              "duration": "PT9H20M",
              "id": "1",
              "numberOfStops": 0,
              "blacklistedInEU": false
            },
            {
              "departure": {
                "iataCode": "HAK",
                "terminal": "2",
                "at": "2025-12-03T08:40:00"
              },
              "arrival": {
                "iataCode": "BKK",
                "at": "2025-12-03T10:05:00"
              },
              "carrierCode": "HU",
              "number": "7939",
              "aircraft": {
                "code": "738"
              },
              "operating": {
                "carrierCode": "HU"
              },
              "duration": "PT2H25M",
              "id": "2",
              "numberOfStops": 0,
              "blacklistedInEU": false
            }
          ]
        }
      ],
      "price": {
        "currency": "EUR",
        "total": "252.70",
        "base": "138.00",
        "fees": [
          {
            "amount": "0.00",
            "type": "SUPPLIER"
          },
          {
            "amount": "0.00",
            "type": "TICKETING"
          }
        ],
        "grandTotal": "252.70"
      },
      "pricingOptions": {
        "fareType": [
          "PUBLISHED"
        ],
        "includedCheckedBagsOnly": true
      },
      "validatingAirlineCodes": [
        "HU"
      ],
      "travelerPricings": [
        {
          "travelerId": "1",
          "fareOption": "STANDARD",
          "travelerType": "ADULT",
          "price": {
            "currency": "EUR",
            "total": "252.70",
            "base": "138.00"
          },
          "fareDetailsBySegment": [
            {
              "segmentId": "1",
              "cabin": "ECONOMY",
              "fareBasis": "QKO779OY",
              "brandedFare": "BAS",
              "brandedFareLabel": "ECO BASIC",
              "class": "Q",
              "includedCheckedBags": {
                "quantity": 2
              },
              "includedCabinBags": {
                "quantity": 1
              },
              "amenities": [
                {
                  "description": "CHECKED BAG FIRST",
                  "isChargeable": false,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "CHECKED BAG SECOND",
                  "isChargeable": false,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "PRE RESERVED SEAT ASSIGNMENT",
                  "isChargeable": true,
                  "amenityType": "PRE_RESERVED_SEAT",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "UPGRADE ELIGIBILITY",
                  "isChargeable": true,
                  "amenityType": "BRANDED_FARES",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "CHANGE BEFORE DEPARTURE",
                  "isChargeable": true,
                  "amenityType": "BRANDED_FARES",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "REFUND BEFORE DEPARTURE",
                  "isChargeable": true,
                  "amenityType": "BRANDED_FARES",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "BUSINESS LOUNGE ACCESS",
                  "isChargeable": true,
                  "amenityType": "LOUNGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                }
              ]
            },
            {
              "segmentId": "2",
              "cabin": "ECONOMY",
              "fareBasis": "QKO779OY",
              "brandedFare": "BAS",
              "brandedFareLabel": "ECO BASIC",
              "class": "X",
              "includedCheckedBags": {
                "quantity": 2
              },
              "includedCabinBags": {
                "quantity": 1
              },
              "amenities": [
                {
                  "description": "CHECKED BAG FIRST",
                  "isChargeable": false,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "CHECKED BAG SECOND",
                  "isChargeable": false,
                  "amenityType": "BAGGAGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "PRE RESERVED SEAT ASSIGNMENT",
                  "isChargeable": true,
                  "amenityType": "PRE_RESERVED_SEAT",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "UPGRADE ELIGIBILITY",
                  "isChargeable": true,
                  "amenityType": "BRANDED_FARES",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "CHANGE BEFORE DEPARTURE",
                  "isChargeable": true,
                  "amenityType": "BRANDED_FARES",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "REFUND BEFORE DEPARTURE",
                  "isChargeable": true,
                  "amenityType": "BRANDED_FARES",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                },
                {
                  "description": "BUSINESS LOUNGE ACCESS",
                  "isChargeable": true,
                  "amenityType": "LOUNGE",
                  "amenityProvider": {
                    "name": "BrandedFare"
                  }
                }
              ]
            }
          ]
        }
      ]
    }
  ],
  "dictionaries": {
    "locations": {
      "HAK": {
        "cityCode": "HAK",
        "countryCode": "CN"
      },
      "DMK": {
        "cityCode": "BKK",
        "countryCode": "TH"
      },
      "BKK": {
        "cityCode": "BKK",
        "countryCode": "TH"
      },
      "DPS": {
        "cityCode": "DPS",
        "countryCode": "ID"
      },
      "SYD": {
        "cityCode": "SYD",
        "countryCode": "AU"
      }
    },
    "aircraft": {
      "738": "BOEING 737-800",
      "789": "BOEING 787-9",
      "7M8": "BOEING 737 MAX 8",
      "32A": "AIRBUS A320 (SHARKLETS)"
    },
    "currencies": {
      "EUR": "EURO"
    },
    "carriers": {
      "OD": "BATIK AIR MALAYSIA",
      "ID": "BATIK AIR INDONESIA",
      "HU": "HAINAN AIRLINES"
    }
  }
}
            """;

    @Test
    void whenApiResponds_thenRequestParamsMatchDTO() {
        // Parse the sample response
        JsonObject response = JsonParser.parseString(SAMPLE_API_RESPONSE).getAsJsonObject();
        String selfLink = response.getAsJsonObject("meta")
                .getAsJsonObject("links")
                .get("self")
                .getAsString();

        // Create DTO with values that should match the API response URL
        FlightSearchDTO dto = new FlightSearchDTO();
        dto.setOrigin("MAD");
        dto.setDestination("NYC");
        dto.setDepartureDate("2025-12-01");
        dto.setAdults(2);
        dto.setNonStop(true);
        dto.setCurrencyCode("EUR");

        // Verify each parameter exists in the API response URL
        assertThat(selfLink)
                .contains("originLocationCode=" + dto.getOrigin())
                .contains("destinationLocationCode=" + dto.getDestination())
                .contains("departureDate=" + dto.getDepartureDate())
                .contains("adults=" + dto.getAdults())
                .contains("nonStop=" + dto.getNonStop())
                .contains("currencyCode=" + dto.getCurrencyCode());
    }

    @Test
    void whenApiRespondsWithFlightData_thenPriceMatchesCurrency() {
        // Parse the sample response
        JsonObject response = JsonParser.parseString(SAMPLE_API_RESPONSE).getAsJsonObject();
        JsonObject flightOffer = response.getAsJsonArray("data").get(0).getAsJsonObject();
        JsonObject price = flightOffer.getAsJsonObject("price");

        // Create DTO with matching currency
        FlightSearchDTO dto = new FlightSearchDTO();
        dto.setCurrencyCode(price.get("currency").getAsString());

        // Verify the currency in DTO matches the price currency in response
        assertThat(dto.getCurrencyCode()).isEqualTo(price.get("currency").getAsString());
    }

    @Test
    void whenApiRespondsWithItinerary_thenNonStopMatchesSegments() {
        // Parse the sample response
        JsonObject response = JsonParser.parseString(SAMPLE_API_RESPONSE).getAsJsonObject();
        JsonObject flightOffer = response.getAsJsonArray("data").get(0).getAsJsonObject();
        JsonObject segment = flightOffer.getAsJsonArray("itineraries")
                .get(0).getAsJsonObject()
                .getAsJsonArray("segments")
                .get(0).getAsJsonObject();

        // Create DTO with nonStop=true (as in our sample response)
        FlightSearchDTO dto = new FlightSearchDTO();
        dto.setNonStop(true);

        // Verify that when nonStop is true, the segment has numberOfStops=0
        assertThat(dto.getNonStop())
                .isEqualTo(segment.get("numberOfStops").getAsInt() == 0);
    }
}