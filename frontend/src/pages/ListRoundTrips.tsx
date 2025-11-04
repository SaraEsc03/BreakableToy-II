import { useNavigate } from "react-router-dom";
import useRoundTripSearch from "../hooks/useRoundTripSearch";
import listBg from "../assets/imgs/listBg.svg";
import { formatTimeShort } from "../utils/formatters/date";
import { formatAmount } from "../utils/formatters/number";
import { getFirstAndLastSegment, getStopsSummary, getDisplayPrice } from "../utils/flightSelectors";
import type { FlightOffer } from "../types/flight";

export default function ListRoundTrips() {
  const navigate = useNavigate();
  const { results, loading, error } = useRoundTripSearch();

  if (loading) return <div className="p-8">Loading round-trip flights…</div>;
  if (error) return <div className="p-8 text-red-600">{error}</div>;
  if (!results || !results.flightOffers || results.flightOffers.length === 0)
    return (
      <div className="p-8">
        <p>No round-trip offers found.</p>
        <button className="mt-4 px-4 py-2 bg-blue-600 text-white rounded" onClick={() => navigate('/search')}>{'< Return to Search'}</button>
      </div>
    );

  return (
    <div style={{ backgroundImage: `url(${listBg})`, backgroundAttachment: 'fixed' }} className="p-8 w-full bg-cover bg-no-repeat bg-bottom bg-fixed bg-gray-50 min-h-screen">
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <button onClick={() => navigate('/search')} className="px-4 py-2 bg-blue-dark text-white rounded-md shadow hover:bg-blue-light transition-colors">&lt; Return to Search</button>
        </div>

        <div className="space-y-6">
          {results.flightOffers.map((offer: FlightOffer) => {
            // For round-trips, itinerary[0] is outbound, itinerary[1] is return (when present)
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
              <div key={offer.id} onClick={() => navigate(`/details/${offer.id}`, { state: { offer } })} className="bg-white/20 rounded-2xl shadow-md p-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4 backdrop-blur-sm cursor-pointer">
                <div className="flex-1 pr-4">
                  <div className="font-semibold text-lg text-blue-dark">{formatTimeShort(outbound?.initialDepartureDateTime)} - {formatTimeShort(outbound?.finalArrivalDateTime)}</div>
                  <div className="text-sm text-gray-700 mt-1">{outFirst?.departureAirport?.name ?? ''} ({outFirst?.departureAirport?.code ?? ''}) - {outLast?.arrivalAirport?.name ?? ''} ({outLast?.arrivalAirport?.code ?? ''})</div>
                  <div className="text-sm text-gray-600 mt-3">{outbound?.totalDuration ?? ''}</div>
                  <div className="text-sm text-gray-700 mt-6">{airlineOut ? <span className="text-blue-dark">{airlineOut.name} ({airlineOut.code})</span> : ''}</div>
                  {operatingOutDifferent ? (
                    <div className="mt-1 text-sm text-gray-600">Operated by: {outFirst?.operatingAirline?.code} · {outFirst?.operatingAirline?.name}</div>
                  ) : null}

                  {inbound ? (
                    <div className="mt-4 border-t pt-4">
                      <div className="font-semibold text-lg text-blue-dark">Return: {formatTimeShort(inbound?.initialDepartureDateTime)} - {formatTimeShort(inbound?.finalArrivalDateTime)}</div>
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
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
