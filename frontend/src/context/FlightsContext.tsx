import { createContext, useState } from "react";
import type { ReactNode } from "react";
import type { FlightOffer } from "../types/flight";

export type FlightsContextType = {
  flights: FlightOffer[];
  setFlights: (f: FlightOffer[]) => void;
  searchParams: URLSearchParams | null;
  setSearchParams: (p: URLSearchParams | null) => void;
};

const FlightsContext = createContext<FlightsContextType | undefined>(undefined);

export const FlightsProvider = ({ children }: { children: ReactNode }) => {
  const [flights, setFlights] = useState<FlightOffer[]>([]);
  const [searchParams, setSearchParams] = useState<URLSearchParams | null>(null);

  return (
    <FlightsContext.Provider value={{ flights, setFlights, searchParams, setSearchParams }}>
      {children}
    </FlightsContext.Provider>
  );
};

export default FlightsContext;
