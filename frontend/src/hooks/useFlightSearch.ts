import useFlightResults from "./useFlightResults";
import type { FlightsResult } from "../types/flight";

export default function useFlightSearch(initialState?: FlightsResult) {
  // thin wrapper for backward compatibility
  const { results, loading, error, refetch } = useFlightResults({ initialState, autoNavigate: true, preferContext: true });
  return { results, loading, error, refetch } as const;
}
