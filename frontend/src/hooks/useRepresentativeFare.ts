import type { TravelerPricing, FareDetails } from "../types/flight";

export default function useRepresentativeFare(travelerPricings?: TravelerPricing[]) {
  const representativeForSegment = (segmentId?: string): FareDetails | undefined => {
    if (!segmentId) return undefined;
    if (!travelerPricings || travelerPricings.length === 0) return undefined;
    const firstTraveler = travelerPricings[0];
    const fares = firstTraveler.fareDetailsBySegment;
    if (!fares) return undefined;
    if (Array.isArray(fares)) return fares.find((f) => String(f.segmentId) === String(segmentId));
    // defensive: older shape where fares might be singular
    return (fares as unknown as FareDetails).segmentId === segmentId ? (fares as unknown as FareDetails) : undefined;
  };

  return { representativeForSegment };
}
