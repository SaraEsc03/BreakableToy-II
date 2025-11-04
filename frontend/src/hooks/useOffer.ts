import { useMemo } from "react";
import { useLocation, useParams } from "react-router-dom";
import { useFlights } from "../context/FlightsContext";
import type { FlightOffer } from "../types/flight";

export default function useOffer() {
  const { id } = useParams<{ id?: string }>();
  const location = useLocation();
  const { flights } = useFlights();

  const offer: FlightOffer | undefined = useMemo(() => {
    const state = (location as unknown as { state?: unknown }).state as
      | undefined
      | { offer?: FlightOffer; flights?: unknown };
    if (state?.offer) return state.offer;
    if (state?.flights) {
      const maybe = state.flights;
      if (Array.isArray(maybe)) return maybe.find((o: FlightOffer) => String(o.id) === String(id));
      const maybeObj = maybe as { flightOffers?: FlightOffer[] };
      if (maybeObj.flightOffers && Array.isArray(maybeObj.flightOffers)) return maybeObj.flightOffers.find((o: FlightOffer) => String(o.id) === String(id));
    }
    if (Array.isArray(flights)) return flights.find((f: FlightOffer) => String(f.id) === String(id));
    return undefined;
  }, [location, flights, id]);

  return { offer };
}
