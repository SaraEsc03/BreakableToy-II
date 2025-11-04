export interface AirportInfo { code?: string; name?: string; }
export interface StopInfo { airport?: AirportInfo; duration?: string; }
export interface AirlineInfo { code?: string; name?: string; }
export interface Segment {
	departureAirport?: AirportInfo;
	arrivalAirport?: AirportInfo;
	id?: string;
	departureDateTime?: string;
	arrivalDateTime?: string;
	airline?: AirlineInfo;
	operatingAirline?: AirlineInfo;
	flightNumber?: string;
	aircraftType?: string;
	duration?: string;
}
export interface Itinerary {
	initialDepartureDateTime?: string;
	finalArrivalDateTime?: string;
	totalDuration?: string;
	segments?: Segment[];
	stopTimes?: StopInfo[];
}
export interface PriceTravelerDetails { currency?: string; total?: string; base?: string; }
export interface Amenity { description?: string; isChargeable?: boolean }
export interface FareDetails { segmentId?: string; cabin?: string; classTrip?: string; amenities?: Amenity[] }
export interface TravelerPricing { travelerId?: string; fareDetailsBySegment?: FareDetails[]; priceTravelerDetails?: PriceTravelerDetails; }
export interface Fee { amount?: string; type?: string }
export interface PriceTotals { currency?: string; total?: string; base?: string; fees?: Fee[]; grandTotal?: string; }
export interface FlightOffer { id?: string; priceTotals?: PriceTotals; travelerPricings?: TravelerPricing[]; itineraries?: Itinerary[]; }
export interface FlightsResult { flightOffers?: FlightOffer[]; }

