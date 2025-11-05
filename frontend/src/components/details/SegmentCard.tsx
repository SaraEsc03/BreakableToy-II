import React from "react";
import { formatDateTime, formatDateShort } from "../../utils/formatters/date";
import { isOperatingDifferent } from "../../utils/segmentUtils";
import FareDetailsPanel from "./FareDetailsPanel";
import type { Segment, TravelerPricing } from "../../types/flight";
import { representativeFareForSegment } from "../../utils/representativeFare";

type Props = { seg: Segment; travelerPricings?: TravelerPricing[]; index?: number };

function SegmentCard({ seg, travelerPricings = [], index }: Props) {
  const fare = representativeFareForSegment(travelerPricings, seg.id);
  const operatingDifferent = isOperatingDifferent(seg);

  return (
    <article key={`segment-${seg.id ?? index}`} className="border-yellow-sun border rounded-lg p-4">
      <div className="flex items-start justify-between">
        <div>
          <div className="text-sm text-gray-500">{formatDateShort(seg.departureDateTime)}</div>
          <div className="font-semibold text-lg text-blue-dark">{formatDateTime(seg.departureDateTime)} — {formatDateTime(seg.arrivalDateTime)}</div>
          <div className="text-sm text-gray-700 mt-1">{seg.departureAirport?.name ?? ''} ({seg.departureAirport?.code ?? ''}) → {seg.arrivalAirport?.name ?? ''} ({seg.arrivalAirport?.code ?? ''})</div>
        </div>
        <div className="text-right">
          <div className="font-medium">{seg.airline?.code ?? ''} · {seg.airline?.name ?? ''}</div>
          <div className="text-sm text-gray-600">{seg.flightNumber ? `Flight ${seg.flightNumber}` : ''}</div>
          <div className="text-xs text-gray-500">Aircraft {seg.aircraftType ?? ''}</div>
        </div>
      </div>

      {operatingDifferent ? (
        <div className="mt-3 text-sm text-gray-600">Operated by: {seg.operatingAirline?.code} · {seg.operatingAirline?.name}</div>
      ) : null}

      <div className="mt-4">
        <div className="font-semibold text-lg text-blue-dark">Fare Details</div>
        <FareDetailsPanel fare={fare} />
      </div>

      {seg.nextLayover ? <div className="mt-3 text-sm text-gray-500">Layover: {seg.nextLayover}</div> : null}
    </article>
  );
}

export default React.memo(SegmentCard);
