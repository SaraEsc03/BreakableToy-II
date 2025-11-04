import { useNavigate } from "react-router-dom";
import useFlightSearch from "../hooks/useFlightSearch";
import listBg from "../assets/imgs/listBg.svg";
import { formatTimeShort } from "../utils/formatters/date";
import { formatAmount } from "../utils/formatters/number";
import { getFirstAndLastSegment, getPrimaryAirline, getStopsSummary, getDisplayPrice } from "../utils/flightSelectors";
import type { FlightOffer } from "../types/flight";

export default function ListSingleTrips() {
  const navigate = useNavigate();

  // use hook to manage parsing, fetching and redirect logic
  const { results, loading, error } = useFlightSearch();

  if (loading) return <div className="p-8">Loading flights…</div>;
  if (error) return <div className="p-8 text-red-600">{error}</div>;
  if (!results || !results.flightOffers || results.flightOffers.length === 0)
    return (
      <div className="p-8">
        <p>No flight offers found.</p>
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
          const itin = offer.itineraries?.[0];
          const segs = itin?.segments ?? [];
          const { first: firstSeg, last: lastSeg } = getFirstAndLastSegment(offer);
          const stopsSummary = getStopsSummary(itin);
          const stops = stopsSummary.details;
          const airline = getPrimaryAirline(offer);
          const displayPrice = getDisplayPrice(offer);

          return (
            <div key={offer.id} className="bg-white/20 rounded-2xl shadow-md p-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4 backdrop-blur-sm">
              <div className="flex-1 pr-4">
                <div className="font-semibold text-lg text-blue-dark">{formatTimeShort(itin?.initialDepartureDateTime)} - {formatTimeShort(itin?.finalArrivalDateTime)}</div>
                <div className="text-sm text-gray-700 mt-1">{firstSeg?.departureAirport?.name ?? ""} ({firstSeg?.departureAirport?.code ?? ""}) - {lastSeg?.arrivalAirport?.name ?? ""} ({lastSeg?.arrivalAirport?.code ?? ""})</div>
                <div className="text-sm text-gray-600 mt-3">{itin?.totalDuration ?? ''}</div>
                <div className="text-sm text-gray-700 mt-6">{airline ? <span className="text-blue-dark">{airline.name} ({airline.code})</span> : ''}</div>
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
            </div>
          );
        })}
      </div>
      </div>
    </div>
  );
}
