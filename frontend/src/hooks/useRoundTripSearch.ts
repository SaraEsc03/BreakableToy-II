import useFlightResults from "./useFlightResults";
import type { FlightsResult } from "../types/flight";

export default function useRoundTripSearch(initialState?: FlightsResult) {
  // thin wrapper for backward compatibility
  const { results, loading, error, refetch } = useFlightResults({ initialState, autoNavigate: false, preferContext: true });
  return { results, loading, error, refetch } as const;
}
