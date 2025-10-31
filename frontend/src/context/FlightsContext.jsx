import { createContext, useState, useContext } from "react";

const FlightsContext = createContext();

export const FlightsProvider = ({ children }) => {
  const [flights, setFlights] = useState([]);
  const [searchParams, setSearchParams] = useState(null);

  return (
    <FlightsContext.Provider value={{ flights, setFlights, searchParams, setSearchParams }}>
      {children}
    </FlightsContext.Provider>
  );
};

export const useFlights = () => useContext(FlightsContext);
