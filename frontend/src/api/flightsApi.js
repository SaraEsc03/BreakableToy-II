import axios from "axios";

// Use Vite env var when available; fallback to localhost
const API_URL = import.meta.env.VITE_FLIGHT_API_URL

export const searchFlights = async (params) => {
  const response = await axios.get(`${API_URL}/search`, { params });
  return response.data;
};
