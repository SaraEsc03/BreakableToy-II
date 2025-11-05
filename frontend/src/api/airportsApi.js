import { get } from "./client";

// Allow runtime configuration via Vite env. Use full endpoint URL or fallback to localhost.
const API_URL = import.meta.env.VITE_AIRPORT_API_URL ?? "http://localhost:8080/api/v1/airports/search";

/**
 * Query backend airport autocomplete.
 * @param {string} query - user query (min 2-3 chars)
 * @param {number} [limit=10] - number of items to return
 * @param {AbortSignal} [signal] - optional abort signal
 * @returns {Promise<Array>} airports array
 */
export const autocompleteAirports = async (query, limit = 10, signal) => {
  if (!query) return [];
  const data = await get(API_URL, { params: { q: query, limit }, signal });
  // Backend returns items with { name, airportCode }.
  // Frontend expects only { name, code } and will concatenate them for display.
  const items = Array.isArray(data) ? data : [];
  return items.map((a) => ({
    name: a.name || a.airportName || a.fullName || "",
    code: a.airportCode || a.code || "",
  }));
};
