import React from "react";
import { formatTimeShort, formatDateShort } from "../../utils/formatters/date";
import { formatAmount } from "../../utils/formatters/number";
import { getFirstAndLastSegment, getPrimaryAirline, getStopsSummary, getDisplayPrice } from "../../utils/flightSelectors";
import type { FlightOffer } from "../../types/flight";

type Props = {
  offer: FlightOffer;
  onOpen: () => void;
  variant?: "single" | "round";
};

function TripCardInner({ offer, onOpen, variant = "single" }: Props) {
  if (!offer) return null;

  if (variant === "round") {
    const outbound = offer.itineraries?.[0];
    const inbound = offer.itineraries?.[1];
    const outboundSegs = outbound?.segments ?? [];
    const inboundSegs = inbound?.segments ?? [];

    const { first: outFirst, last: outLast } = getFirstAndLastSegment({ ...offer, itineraries: [outbound] } as FlightOffer);
    const { first: inFirst, last: inLast } = getFirstAndLastSegment({ ...offer, itineraries: [inbound] } as FlightOffer);

    const outStops = getStopsSummary(outbound);
    const inStops = getStopsSummary(inbound);

    const airlineOut = outFirst?.airline ?? outFirst?.operatingAirline;
    const airlineIn = inFirst?.airline ?? inFirst?.operatingAirline;

    const displayPrice = getDisplayPrice(offer);

    const operatingOutDifferent = outFirst?.operatingAirline?.code && outFirst?.operatingAirline?.code !== outFirst?.airline?.code;
    const operatingInDifferent = inFirst?.operatingAirline?.code && inFirst?.operatingAirline?.code !== inFirst?.airline?.code;

    return (
      <button type="button" onClick={onOpen} className="w-full text-left bg-white/20 rounded-2xl shadow-md p-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4 backdrop-blur-sm cursor-pointer">
        <div className="flex-1 pr-4">
          <div className="font-semibold text-lg text-blue-dark">{formatDateShort(outbound?.initialDepartureDateTime)} {formatTimeShort(outbound?.initialDepartureDateTime)} - {formatDateShort(outbound?.finalArrivalDateTime)} {formatTimeShort(outbound?.finalArrivalDateTime)}</div>
          <div className="text-sm text-gray-700 mt-1">{outFirst?.departureAirport?.name ?? ''} ({outFirst?.departureAirport?.code ?? ''}) - {outLast?.arrivalAirport?.name ?? ''} ({outLast?.arrivalAirport?.code ?? ''})</div>
          <div className="text-sm text-gray-600 mt-3">{outbound?.totalDuration ?? ''}</div>
          <div className="text-sm text-gray-700 mt-6">{airlineOut ? <span className="text-blue-dark">{airlineOut.name} ({airlineOut.code})</span> : ''}</div>
          {operatingOutDifferent ? (
            <div className="mt-1 text-sm text-gray-600">Operated by: {outFirst?.operatingAirline?.code} · {outFirst?.operatingAirline?.name}</div>
          ) : null}

          {inbound ? (
            <div className="mt-4 border-t pt-4">
              <div className="font-semibold text-lg text-blue-dark">{formatDateShort(inbound?.initialDepartureDateTime)} {formatTimeShort(inbound?.initialDepartureDateTime)} - {formatDateShort(inbound?.finalArrivalDateTime)} {formatTimeShort(inbound?.finalArrivalDateTime)}</div>
              <div className="text-sm text-gray-700 mt-1">{inFirst?.departureAirport?.name ?? ''} ({inFirst?.departureAirport?.code ?? ''}) - {inLast?.arrivalAirport?.name ?? ''} ({inLast?.arrivalAirport?.code ?? ''})</div>
              <div className="text-sm text-gray-600 mt-3">{inbound?.totalDuration ?? ''}</div>
              <div className="text-sm text-gray-700 mt-6">{airlineIn ? <span className="text-blue-dark">{airlineIn.name} ({airlineIn.code})</span> : ''}</div>
              {operatingInDifferent ? (
                <div className="mt-1 text-sm text-gray-600">Operated by: {inFirst?.operatingAirline?.code} · {inFirst?.operatingAirline?.name}</div>
              ) : null}
            </div>
          ) : null}
        </div>

        <div className="md:w-80 shrink-0 flex flex-col items-start justify-center text-left px-6">
          <div className="text-sm font-medium text-gray-800">Outbound: {outStops.count > 0 ? `${outStops.count} stop${outStops.count > 1 ? 's' : ''}` : 'Non-stop'}</div>
          {outStops.count > 0 ? (
            <div className="text-sm text-gray-600 mt-2 leading-relaxed w-full">
              {outStops.details.map((s, idx) => {
                const nextArrival = outboundSegs[idx + 1]?.arrivalAirport;
                return (
                  <div key={`o-${idx}`} className="flex flex-col items-start">
                    <div className="text-sm font-medium">{s.duration}</div>
                    <div className="text-sm text-gray-600 w-full">{s.airport?.name ?? ''} ({s.airport?.code ?? ''}){nextArrival ? ` - ${nextArrival.name} (${nextArrival.code})` : ''}</div>
                  </div>
                );
              })}
            </div>
          ) : null}

          {inbound ? (
            <div className="mt-4 w-full">
              <div className="text-sm font-medium text-gray-800">Inbound: {inStops.count > 0 ? `${inStops.count} stop${inStops.count > 1 ? 's' : ''}` : 'Non-stop'}</div>
              {inStops.count > 0 ? (
                <div className="text-sm text-gray-600 mt-2 leading-relaxed w-full">
                  {inStops.details.map((s, idx) => {
                    const nextArrival = inboundSegs[idx + 1]?.arrivalAirport;
                    return (
                      <div key={`i-${idx}`} className="flex flex-col items-start">
                        <div className="text-sm font-medium">{s.duration}</div>
                        <div className="text-sm text-gray-600 w-full">{s.airport?.name ?? ''} ({s.airport?.code ?? ''}){nextArrival ? ` - ${nextArrival.name} (${nextArrival.code})` : ''}</div>
                      </div>
                    );
                  })}
                </div>
              ) : null}
            </div>
          ) : null}
        </div>

        <div className="md:w-48 shrink-0 text-right">
          <div className="text-2xl text-blue-dark font-bold">{displayPrice.total ? `${displayPrice.currency ?? ''} ${formatAmount(displayPrice.total)}` : ''}</div>
          <div className="text-sm text-gray-500 mt-1 font-semibold">TOTAL</div>
          <div className="text-lg font-medium mt-3">{offer.travelerPricings && offer.travelerPricings[0] && offer.travelerPricings[0].priceTravelerDetails ? `${offer.travelerPricings[0].priceTravelerDetails.currency} ${formatAmount(offer.travelerPricings[0].priceTravelerDetails.total)}` : ''}</div>
          <div className="text-sm text-gray-500">Per Traveler</div>
        </div>
      </button>
    );
  }

  // single-trip rendering
  const itin = offer.itineraries?.[0];
  const segs = itin?.segments ?? [];
  const { first: firstSeg, last: lastSeg } = getFirstAndLastSegment(offer);
  const stopsSummary = getStopsSummary(itin);
  const stops = stopsSummary.details;
  const airline = getPrimaryAirline(offer);
  const displayPrice = getDisplayPrice(offer);

  return (
    <button type="button" onClick={onOpen} className="w-full text-left bg-white/20 rounded-2xl shadow-md p-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4 backdrop-blur-sm cursor-pointer">
      <div className="flex-1 pr-4">
        <div className="font-semibold text-lg text-blue-dark">{formatDateShort(itin?.initialDepartureDateTime)} {formatTimeShort(itin?.initialDepartureDateTime)} - {formatDateShort(itin?.finalArrivalDateTime)} {formatTimeShort(itin?.finalArrivalDateTime)}</div>
        <div className="text-sm text-gray-700 mt-1">{firstSeg?.departureAirport?.name ?? ""} ({firstSeg?.departureAirport?.code ?? ""}) - {lastSeg?.arrivalAirport?.name ?? ""} ({lastSeg?.arrivalAirport?.code ?? ""})</div>
        <div className="text-sm text-gray-600 mt-3">{itin?.totalDuration ?? ''}</div>
        <div className="text-sm text-gray-700 mt-6">{airline ? <span className="text-blue-dark">{airline.name} ({airline.code})</span> : ''}</div>
        {/* show operating carrier when it's different from the marketing airline */}
        {firstSeg?.operatingAirline?.code && firstSeg?.operatingAirline?.code !== firstSeg?.airline?.code ? (
          <div className="mt-1 text-sm text-gray-600">Operated by: {firstSeg.operatingAirline.code} · {firstSeg.operatingAirline.name}</div>
        ) : null}
      </div>

      <div className="md:w-80 shrink-0 flex flex-col items-start justify-center text-left px-6">
        <div className="text-sm font-medium text-gray-800">{stopsSummary.count > 0 ? `${stopsSummary.count} stop${stopsSummary.count > 1 ? 's' : ''}` : 'Non-stop'}</div>
        <div className="text-sm text-gray-600 mt-2 leading-relaxed w-full">
          {stops.length > 0 ? (
            <div className="space-y-2">
              {stops.map((s, idx) => {
                const nextArrival = segs[idx + 1]?.arrivalAirport;
                return (
                  <div key={idx} className="flex flex-col items-start">
                    <div className="text-sm font-medium">{s.duration}</div>
                    <div className="text-sm text-gray-600 w-full">{s.airport?.name ?? ''} ({s.airport?.code ?? ''}){nextArrival ? ` - ${nextArrival.name} (${nextArrival.code})` : ''}</div>
                  </div>
                );
              })}
            </div>
          ) : null}
        </div>
      </div>

      <div className="md:w-48 shrink-0 text-right">
        <div className="text-2xl text-blue-dark font-bold">{displayPrice.total ? `${displayPrice.currency ?? ''} ${formatAmount(displayPrice.total)}` : ''}</div>
        <div className="text-sm text-gray-500 mt-1 font-semibold">TOTAL</div>
        <div className="text-lg font-medium mt-3">{offer.travelerPricings && offer.travelerPricings[0] && offer.travelerPricings[0].priceTravelerDetails ? `${offer.travelerPricings[0].priceTravelerDetails.currency} ${formatAmount(offer.travelerPricings[0].priceTravelerDetails.total)}` : ''}</div>
        <div className="text-sm text-gray-500">Per Traveler</div>
      </div>
    </button>
  );
}

export default React.memo(TripCardInner);
