import { createSlice } from "@reduxjs/toolkit";


const flightsSlice = createSlice({
  name: "flights",
  initialState: {
    list: [],
    selected: null,
  },
  reducers: {
    setFlights: (state, action) => {
      state.list = action.payload;
    },
    selectFlight: (state, action) => {
      state.selected = action.payload;
    },
  },
});

export const { setFlights, selectFlight } = flightsSlice.actions;
export default flightsSlice.reducer;
