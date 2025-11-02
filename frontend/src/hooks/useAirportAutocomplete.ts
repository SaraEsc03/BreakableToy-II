import { useEffect, useMemo, useRef, useState } from "react";
import { autocompleteAirports } from "../api/airportsApi";

export type Airport = {
  code: string; // IATA
  name: string;
  city?: string;
  country?: string;
};

type Options = {
  minLength?: number;
  debounceMs?: number;
  limit?: number;
}; 

type BackendAirport = Partial<Airport> & { code?: string; airportCode?: string; name?: string; city?: string; country?: string };

export function useAirportAutocomplete(options: Options = {}) {
  const { minLength = 2, debounceMs = 300, limit = 10 } = options;

  const [inputValue, setInputValue] = useState("");
  const [results, setResults] = useState<Airport[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState<number>(-1);

  const abortRef = useRef<AbortController | null>(null);
  const requestIdRef = useRef(0);
  // When the component programmatically sets the input (e.g. on selection), we
  // may want to avoid triggering a fetch for that value. Use this flag to
  // ignore the next fetch cycle.
  const ignoreNextFetchRef = useRef(false);

  const normalized = inputValue.trim().toLowerCase();

  const fetchData = useMemo(
    () =>
      async (query: string, reqId: number) => {
        try {
          setError("");
          setLoading(true);

          abortRef.current?.abort();
          abortRef.current = new AbortController();

          const data: unknown = await autocompleteAirports(query, limit, abortRef.current.signal);

          // Only keep array items that have a code
          const arr = Array.isArray(data) ? (data as BackendAirport[]) : [];
          const clean: Airport[] = arr
            .filter((a) => Boolean(a && (a.code || a.airportCode)))
            .map((a) => ({
              code: (a.code ?? a.airportCode) as string,
              name: a.name ?? "",
              city: a.city,
              country: a.country,
            }));

          // Only apply if latest
          if (requestIdRef.current === reqId) {
            setResults(clean);
          }
        } catch (err: unknown) {
          const e = err as { name?: string; code?: string };
          if (e?.name === "CanceledError" || e?.code === "ERR_CANCELED") return;
          setError("Unable to load airports");
        } finally {
          if (requestIdRef.current === reqId) setLoading(false);
        }
      },
    [limit]
  );

  useEffect(() => {
    if (normalized.length < minLength) {
      setResults([]);
      setOpen(false);
      setHighlightedIndex(-1);
      return;
    }

    if (ignoreNextFetchRef.current) {
      ignoreNextFetchRef.current = false;
      return;
    }

    setOpen(true);
    const id = ++requestIdRef.current;
    const t = setTimeout(() => fetchData(normalized, id), debounceMs);
    return () => clearTimeout(t);
  }, [normalized, minLength, debounceMs, fetchData]);

  const moveHighlight = (delta: number) => {
    setHighlightedIndex((prev) => {
      const next = results.length === 0 ? -1 : (prev + delta + results.length) % results.length;
      return next;
    });
  };

  return {
    inputValue,
    setInputValue,
    // Expose a setter that skips the next fetch cycle. Use when programmatically
    // updating input (for example, on selection) to avoid an unnecessary request.
    setInputValueNoFetch: (v: string) => {
      ignoreNextFetchRef.current = true;
      setInputValue(v);
    },
    results,
    loading,
    error,
    open,
    setOpen,
    highlightedIndex,
    moveHighlight,
  };
}
