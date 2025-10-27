// src/store/flightsSlice.ts
import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";
import type { Flight } from "../types/flight";

interface FlightState {
  searchResults: Flight[];   // aquí irán los vuelos obtenidos de la API
  selectedFlight: Flight | null;
  loading: boolean;
}

const initialState: FlightState = {
  searchResults: [],
  selectedFlight: null,
  loading: false,
};

const flightsSlice = createSlice({
  name: "flights",
  initialState,
  reducers: {
    setSearchResults(state, action: PayloadAction<Flight[]>) {
      state.searchResults = action.payload;
    },
    selectFlight(state, action: PayloadAction<Flight>) {
      state.selectedFlight = action.payload;
    },
    setLoading(state, action: PayloadAction<boolean>) {
      state.loading = action.payload;
    },
    clearSelectedFlight(state) {
      state.selectedFlight = null;
    },
  },
});

export const { setSearchResults, selectFlight, setLoading, clearSelectedFlight } =
  flightsSlice.actions;

export default flightsSlice.reducer;
