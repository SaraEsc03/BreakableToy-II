export interface AirportInfo { code?: string; name?: string; }
export interface StopInfo { airport?: AirportInfo; duration?: string; }
export interface AirlineInfo { code?: string; name?: string; }
export interface Segment {
	departureAirport?: AirportInfo;
	arrivalAirport?: AirportInfo;
	departureDateTime?: string;
	arrivalDateTime?: string;
	airline?: AirlineInfo;
	operatingAirline?: AirlineInfo;
}
export interface Itinerary {
	initialDepartureDateTime?: string;
	finalArrivalDateTime?: string;
	totalDuration?: string;
	segments?: Segment[];
	stopTimes?: StopInfo[];
}
export interface PriceTravelerDetails { currency?: string; total?: string; base?: string; }
export interface TravelerPricing { travelerId?: string; fareDetailsBySegment?: unknown; priceTravelerDetails?: PriceTravelerDetails; }
export interface PriceTotals { currency?: string; total?: string; base?: string; grandTotal?: string; }
export interface FlightOffer { id?: string; priceTotals?: PriceTotals; travelerPricings?: TravelerPricing[]; itineraries?: Itinerary[]; }
export interface FlightsResult { flightOffers?: FlightOffer[]; }

