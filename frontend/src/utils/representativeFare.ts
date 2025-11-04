import type { FareDetails, TravelerPricing } from "../types/flight";

/**
 * Pure utility to find the representative fare details for a given segment.
 * Keeps the logic simple and testable (no React hook required).
 */
export function representativeFareForSegment(travelerPricings?: TravelerPricing[], segmentId?: string): FareDetails | undefined {
  if (!segmentId) return undefined;
  if (!travelerPricings || travelerPricings.length === 0) return undefined;
  const firstTraveler = travelerPricings[0];
  const fares = firstTraveler.fareDetailsBySegment;
  if (!fares) return undefined;
  if (Array.isArray(fares)) return fares.find((f) => String(f.segmentId) === String(segmentId));
  // Defensive: older shape where fares might be singular
  return (fares as unknown as FareDetails).segmentId === segmentId ? (fares as unknown as FareDetails) : undefined;
}
