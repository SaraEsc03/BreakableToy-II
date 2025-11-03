import { useEffect, useState, useCallback } from "react";
import { useLocation, useSearchParams } from "react-router-dom";
import { searchFlights } from "../api/flightsApi";
import { toIsoDate } from "../utils/formatters/date";
import type { FlightsResult } from "../types/flight";

export default function useRoundTripSearch(initialState?: FlightsResult) {
  const location = useLocation();
  const [searchParams] = useSearchParams();

  const [results, setResults] = useState<FlightsResult | null>(initialState ?? null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const searchString = searchParams.toString();

  const fetchData = useCallback(async () => {
    const paramsUrl = new URLSearchParams(searchString);
    const origin = paramsUrl.get("origin");
    const destination = paramsUrl.get("destination");
    const departureDateRaw = paramsUrl.get("departureDate");
    const returnDateRaw = paramsUrl.get("returnDate");

    if (!origin || !destination || !departureDateRaw || !returnDateRaw) return;

    const departureDate = departureDateRaw.includes("/") ? toIsoDate(departureDateRaw) : departureDateRaw;
    const returnDate = returnDateRaw.includes("/") ? toIsoDate(returnDateRaw) : returnDateRaw;

    const apiParams = {
      origin,
      destination,
      departureDate,
      returnDate,
      currencyCode: paramsUrl.get("currencyCode") || undefined,
      nonStop: paramsUrl.get("nonStop") === "true" ? true : undefined,
      adults: paramsUrl.get("adults") ? Number(paramsUrl.get("adults")) : undefined,
    };

    try {
      setLoading(true);
      setError("");
      const data = await searchFlights(apiParams);
      setResults(data);
    } catch (e) {
      console.error(e);
      setError("Failed to load round-trip flights");
    } finally {
      setLoading(false);
    }
  }, [searchString]);

  useEffect(() => {
    const state = location.state as unknown as { flights?: FlightsResult } | null;
    if (state && state.flights) {
      setResults(state.flights);
      return;
    }

    fetchData();
  }, [location.state, fetchData]);

  return { results, loading, error, refetch: fetchData } as const;
}
