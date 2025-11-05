import type { TravelerPricing, FareDetails } from "../types/flight";

/**
 * Pure utility to get the representative fare details for a given segment
 * from the first traveler pricing (if present).
 */
export function representativeFareForSegment(
  travelerPricings?: TravelerPricing[] | null,
  segmentId?: string | number | null
): FareDetails | undefined {
  if (!segmentId) return undefined;
  if (!travelerPricings || travelerPricings.length === 0) return undefined;

  const firstTraveler = travelerPricings[0];
  const fares = firstTraveler?.fareDetailsBySegment;
  if (!fares) return undefined;

  if (Array.isArray(fares)) {
    return fares.find((f) => String(f.segmentId) === String(segmentId));
  }

  // defensive: older shape where fares might be singular object
  const asFare = fares as unknown as FareDetails;
  return asFare && String(asFare.segmentId) === String(segmentId) ? asFare : undefined;
}

export default representativeFareForSegment;
