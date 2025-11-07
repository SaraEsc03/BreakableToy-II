import { useContext } from "react";
import FlightsContext from "./FlightsContext";
import type { FlightsContextType } from "./FlightsContext";

export const useFlights = (): FlightsContextType => {
  const ctx = useContext(FlightsContext) as FlightsContextType | undefined;
  if (!ctx) throw new Error("useFlights must be used within a FlightsProvider");
  return ctx;
};

