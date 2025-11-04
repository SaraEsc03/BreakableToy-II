import type { FlightOffer, Segment } from "../types/flight";

export function flattenSegments(offer?: FlightOffer): { seg: Segment; itineraryIndex: number; segmentIndex: number }[] {
  const out: { seg: Segment; itineraryIndex: number; segmentIndex: number }[] = [];
  if (!offer) return out;
  (offer.itineraries ?? []).forEach((iti, itiIdx) => {
    (iti.segments ?? []).forEach((s, segIdx) => out.push({ seg: s, itineraryIndex: itiIdx, segmentIndex: segIdx }));
  });
  return out;
}

export function isOperatingDifferent(seg?: Segment): boolean {
  return !!(seg && seg.operatingAirline?.code && seg.operatingAirline.code !== seg.airline?.code);
}
