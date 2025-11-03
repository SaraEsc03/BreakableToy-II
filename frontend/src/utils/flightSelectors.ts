import type { FlightOffer, Itinerary, Segment, StopInfo } from "../types/flight";

/**
 * Return the first and last segment of the primary itinerary (index 0) for an offer.
 */
export function getFirstAndLastSegment(offer: FlightOffer): { first?: Segment; last?: Segment } {
  const itin: Itinerary | undefined = offer.itineraries?.[0];
  const segs = itin?.segments ?? [];
  return { first: segs[0], last: segs[segs.length - 1] };
}

/**
 * Determine the primary airline for an offer using the first segment's airline or operatingAirline.
 */
export function getPrimaryAirline(offer: FlightOffer) {
  const { first } = getFirstAndLastSegment(offer);
  return first?.airline ?? first?.operatingAirline;
}

/**
 * Return a summary object for stops for a given itinerary.
 * If no itinerary is provided, returns count 0 and empty details.
 */
export function getStopsSummary(itin?: Itinerary): { count: number; details: StopInfo[] } {
  const stops: StopInfo[] = itin?.stopTimes ?? [];
  return { count: stops.length, details: stops };
}

/**
 * Choose a display price for an offer. Prefers `priceTotals`, falls back to the first traveler's `priceTravelerDetails`.
 * Returns consistent shape with optional fields so callers can format/fallback as needed.
 */
export function getDisplayPrice(offer: FlightOffer): { currency?: string; total?: string } {
  if (offer.priceTotals && (offer.priceTotals.currency || offer.priceTotals.total)) {
    return { currency: offer.priceTotals.currency, total: offer.priceTotals.total };
  }

  const travelerPrice = offer.travelerPricings && offer.travelerPricings[0] && offer.travelerPricings[0].priceTravelerDetails;
  if (travelerPrice && (travelerPrice.currency || travelerPrice.total)) {
    return { currency: travelerPrice.currency, total: travelerPrice.total };
  }

  return {};
}
