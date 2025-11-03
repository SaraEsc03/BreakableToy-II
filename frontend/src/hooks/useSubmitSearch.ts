import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { searchFlights } from "../api/flightsApi";
import type { SearchFormValues, SearchApiParams } from "../types/search";
import { useFlights } from "../context/FlightsContext";

function toMmDdYyyy(iso?: string) {
  if (!iso) return undefined;
  if (iso.includes("/")) return iso;
  const parts = iso.split("-");
  if (parts.length !== 3) return iso;
  const [y, m, d] = parts;
  return `${m}/${d}/${y}`;
}

export default function useSubmitSearch() {
  const navigate = useNavigate();
  const { setFlights, setSearchParams } = useFlights();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async (values: SearchFormValues) => {
    setError(null);
    const apiParams: SearchApiParams = {
      origin: values.departureAirport,
      destination: values.arrivalAirport,
      departureDate: values.departureDate,
      returnDate: values.returnDate || undefined,
      currencyCode: values.currency || undefined,
      nonStop: values.nonStop || undefined,
      adults: values.adults || undefined,
    };

    setLoading(true);
    try {
      const results = await searchFlights(apiParams);
      // store in context for global access
      setFlights(results.flightOffers ?? []);

      // build deep-link query (dates in mm/dd/yyyy for compatibility with current UI)
      const params = new URLSearchParams();
      params.set("origin", apiParams.origin);
      params.set("destination", apiParams.destination);
      const dep = toMmDdYyyy(apiParams.departureDate);
      if (dep) params.set("departureDate", dep);
      const ret = apiParams.returnDate ? toMmDdYyyy(apiParams.returnDate) : undefined;
      if (ret) params.set("returnDate", ret);
      if (apiParams.currencyCode) params.set("currencyCode", apiParams.currencyCode);
      if (apiParams.nonStop) params.set("nonStop", String(apiParams.nonStop));
      if (apiParams.adults) params.set("adults", String(apiParams.adults));

      // keep deep-link params in context too
      setSearchParams(params);

      if (apiParams.returnDate) {
        navigate(`/round-trips?${params.toString()}`);
      } else {
        navigate(`/single-trips?${params.toString()}`, { state: { flights: results } });
      }
    } catch (e) {
      console.error(e);
      setError("Failed to search flights. Please try again.");
      throw e;
    } finally {
      setLoading(false);
    }
  };

  return { submit, loading, error } as const;
}
