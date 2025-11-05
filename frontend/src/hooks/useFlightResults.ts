import { useEffect, useState, useCallback, useRef } from "react";
import { useLocation, useSearchParams, useNavigate } from "react-router-dom";
import { searchFlights } from "../api/flightsApi";
import type { FlightsResult } from "../types/flight";
import type { SearchApiParams } from "../utils/searchParams";
import { parseSearchParams } from "../utils/searchParams";
import { useFlights } from "../context/useFlights";

type UseFlightResultsOptions = {
  initialState?: FlightsResult | null;
  autoNavigate?: boolean; // whether to redirect single->round when returnDate present
  preferContext?: boolean; // whether to set results into FlightsContext
};

export default function useFlightResults(opts: UseFlightResultsOptions = {}) {
  const { initialState = null, autoNavigate = true, preferContext = true } = opts;
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { setFlights } = useFlights();

  const [results, setResults] = useState<FlightsResult | null>(initialState ?? null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const searchString = searchParams.toString();

  const abortRef = useRef<AbortController | null>(null);

  const refetch = useCallback(async (): Promise<void> => {
    const parsed = parseSearchParams(searchString);
    if (parsed.mode === "none" || !parsed.apiParams) return;

    // if single and autoNavigate is true but apiParams has return date, redirect
    if (autoNavigate && parsed.mode === "single" && parsed.apiParams.returnDate) {
      navigate(`/round-trips?${searchString}`);
      return;
    }

    // abort any previous request
    abortRef.current?.abort();

    const controller = new AbortController();
    abortRef.current = controller;

    try {
      setLoading(true);
      setError(null);
      const data = await searchFlights(parsed.apiParams as SearchApiParams, { signal: controller.signal });
      // only update state if request was not aborted
      if (!controller.signal.aborted) {
        setResults(data);
        if (preferContext) {
          setFlights(data.flightOffers ?? []);
        }
      }
    } catch (e: unknown) {
      const err = e as { name?: string; code?: string; message?: string };
      // ignore abort errors
      if (err?.name === "CanceledError" || err?.code === "ERR_CANCELED" || err?.message === "canceled") {
        // request was cancelled, do nothing
      } else {
        console.error(e);
        setError("Failed to load flights");
      }
    } finally {
      // only clear loading if current controller hasn't been replaced
      if (abortRef.current === controller) {
        setLoading(false);
        abortRef.current = null;
      }
    }
  }, [searchString, navigate, autoNavigate, preferContext, setFlights]);

  useEffect(() => {
    // if navigation passed flights in state, use them as initial data
    const state = location.state as unknown as { flights?: FlightsResult } | null;
    if (state && state.flights) {
      setResults(state.flights);
      return;
    }
    refetch();
  }, [location.state, refetch]);

  return { results, loading, error, refetch } as const;
}
