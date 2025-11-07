import { get } from "./client";

// Use Vite env var when available; fallback to localhost
const API_URL = import.meta.env.VITE_FLIGHT_API_URL ?? "http://localhost:8080/api/v1/flights";

/**
 * Search flights. Accepts optional options with AbortSignal: { signal }
 */
export const searchFlights = async (params, { signal } = {}) => {
  const data = await get(`${API_URL}/search`, { params, signal });
  return data;
};
 