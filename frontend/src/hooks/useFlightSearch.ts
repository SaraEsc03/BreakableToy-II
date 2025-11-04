import { useEffect, useState, useCallback } from "react";
import { useLocation, useSearchParams, useNavigate } from "react-router-dom";
import { searchFlights } from "../api/flightsApi";
import { toIsoDate } from "../utils/formatters/date";
import type { FlightsResult } from "../types/flight";

export default function useFlightSearch(initialState?: FlightsResult) {
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [results, setResults] = useState<FlightsResult | null>(initialState ?? null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Keep a stable string representation for deps
  const searchString = searchParams.toString();

  const fetchData = useCallback(async () => {
    const paramsUrl = new URLSearchParams(searchString);
    const origin = paramsUrl.get("origin");
    const destination = paramsUrl.get("destination");
    const departureDateRaw = paramsUrl.get("departureDate");

    if (!origin || !destination || !departureDateRaw) {
      // Nothing to do (no deep-link)
      return;
    }

    const departureDate = departureDateRaw.includes("/") ? toIsoDate(departureDateRaw) : departureDateRaw;

    const returnDateRaw = paramsUrl.get("returnDate");
    if (returnDateRaw) {
      // round-trip: redirect to the round-trips page (fallback)
      navigate(`/round-trips?${searchString}`);
      return;
    }

    const returnDate = returnDateRaw
      ? returnDateRaw.includes("/")
        ? toIsoDate(returnDateRaw)
        : returnDateRaw
      : undefined;

    const apiParams = {
      origin,
      destination,
      departureDate,
      currencyCode: paramsUrl.get("currencyCode") || undefined,
      returnDate,
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
      setError("Failed to load flights");
    } finally {
      setLoading(false);
    }
  }, [searchString, navigate]);

  useEffect(() => {
    // If navigation passed flights in state, use them as initial data
    const state = location.state as unknown as { flights?: FlightsResult } | null;
    if (state && state.flights) {
      setResults(state.flights);
      return;
    }

  // Otherwise, fetch based on URL params (if present)
  fetchData();
  }, [location.state, fetchData]);

  return { results, loading, error, refetch: fetchData } as const;
}
