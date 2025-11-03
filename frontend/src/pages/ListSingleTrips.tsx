import { useEffect, useState } from "react";
import { useLocation, useSearchParams, useNavigate } from "react-router-dom";
import { searchFlights } from "../api/flightsApi";
import listBg from "../assets/imgs/listBg.svg";

// Minimal typing for FlightsResult shape returned by backend
type AirportInfo = { code?: string; name?: string };
type StopInfo = { airport?: AirportInfo; duration?: string };
type AirlineInfo = { code?: string; name?: string };
type Segment = { departureAirport?: AirportInfo; arrivalAirport?: AirportInfo; departureDateTime?: string; arrivalDateTime?: string; airline?: AirlineInfo; operatingAirline?: AirlineInfo };
type Itinerary = { initialDepartureDateTime?: string; finalArrivalDateTime?: string; totalDuration?: string; segments?: Segment[]; stopTimes?: StopInfo[] };
type PriceTravelerDetails = { currency?: string; total?: string; base?: string };
type TravelerPricing = { travelerId?: string; fareDetailsBySegment?: unknown; priceTravelerDetails?: PriceTravelerDetails };
type PriceTotals = { currency?: string; total?: string; base?: string; grandTotal?: string };

type FlightOffer = {
  id?: string;
  priceTotals?: PriceTotals;
  travelerPricings?: TravelerPricing[];
  itineraries?: Itinerary[];
};

type FlightsResult = { flightOffers?: FlightOffer[] };

function toIsoDate(mmddyyyy: string | null): string | null {
  if (!mmddyyyy) return mmddyyyy;
  const parts = mmddyyyy.split("/");
  if (parts.length !== 3) return mmddyyyy;
  const [mm, dd, yyyy] = parts;
  return `${yyyy}-${mm.padStart(2, "0")}-${dd.padStart(2, "0")}`;
}

function formatTimeShort(iso?: string) {
  if (!iso) return "";
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleTimeString(undefined, { hour: "numeric", minute: "2-digit" });
}

function formatAmount(amount?: string) {
  if (!amount) return "";
  // remove existing commas, parse number
  const n = Number(String(amount).replace(/,/g, ""));
  if (Number.isNaN(n)) return amount;
  return n.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

export default function ListSingleTrips() {
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [results, setResults] = useState<FlightsResult | null>(null);

  useEffect(() => {
    // If navigation passed flights in state, use them as initial data
    const state = location.state as unknown as { flights?: FlightsResult } | null;
    if (state && state.flights) {
      setResults(state.flights);
    }

    // If there are query params, perform a fetch using them (preferred deep-link)
  const origin = searchParams.get("origin");
  const destination = searchParams.get("destination");
  const departureDateRaw = searchParams.get("departureDate");

    if (!origin || !destination || !departureDateRaw) {
      // if we don't have required params and no state, show nothing (user can come from search)
      return;
    }

    // convert frontend mm/dd/yyyy to yyyy-mm-dd if needed
    const departureDate = departureDateRaw.includes("/")
      ? toIsoDate(departureDateRaw)
      : departureDateRaw;

    const returnDateRaw = searchParams.get("returnDate");
    // If the URL contains a returnDate, this is a round-trip request — redirect to the Round Trips fallback
    if (returnDateRaw) {
      navigate(`/round-trips?${searchParams.toString()}`);
      return;
    }
    const returnDate = returnDateRaw
      ? returnDateRaw.includes("/")
        ? toIsoDate(returnDateRaw)
        : returnDateRaw
      : undefined;

    const params = {
      origin,
      destination,
      departureDate,
      currencyCode: searchParams.get("currencyCode") || undefined,
      returnDate,
      nonStop: searchParams.get("nonStop") === "true" ? true : undefined,
      adults: searchParams.get("adults") ? Number(searchParams.get("adults")) : undefined,
    };

    // fetch
    (async () => {
      try {
        setLoading(true);
        setError("");
        const data = await searchFlights(params);
        setResults(data);
      } catch (e) {
        console.error(e);
        setError("Failed to load flights");
      } finally {
        setLoading(false);
      }
    })();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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
        {results.flightOffers.map((offer) => {
          const itin = offer.itineraries?.[0];
          const segs = itin?.segments ?? [];
          const firstSeg = segs[0];
          const lastSeg = segs[segs.length - 1];
          const stops = itin?.stopTimes ?? [];
          const airline = firstSeg?.airline ?? firstSeg?.operatingAirline;

          return (
            <div key={offer.id} className="bg-white/20 rounded-2xl shadow-md p-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4 backdrop-blur-sm">
              <div className="flex-1 pr-4">
                <div className="font-semibold text-lg text-blue-dark">{formatTimeShort(itin?.initialDepartureDateTime)} - {formatTimeShort(itin?.finalArrivalDateTime)}</div>
                <div className="text-sm text-gray-700 mt-1">{firstSeg?.departureAirport?.name ?? ""} ({firstSeg?.departureAirport?.code ?? ""}) - {lastSeg?.arrivalAirport?.name ?? ""} ({lastSeg?.arrivalAirport?.code ?? ""})</div>
                <div className="text-sm text-gray-600 mt-3">{itin?.totalDuration ?? ''}</div>
                <div className="text-sm text-gray-700 mt-6">{airline ? <span className="text-blue-dark">{airline.name} ({airline.code})</span> : ''}</div>
              </div>

              <div className="md:w-80 shrink-0 flex flex-col items-start justify-center text-left px-6">
                <div className="text-sm font-medium text-gray-800">{stops.length > 0 ? `${stops.length} stop${stops.length > 1 ? 's' : ''}` : 'Non-stop'}</div>
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
                <div className="text-2xl text-blue-dark font-bold">{offer.priceTotals ? `${offer.priceTotals.currency} ${formatAmount(offer.priceTotals.total)}` : ''}</div>
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
