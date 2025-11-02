import axios from "axios";

// Backend exposes the v1 endpoint used in tests:
// GET /api/v1/airports/search?q=<term>&limit=<n>
const API_URL = "http://localhost:8080/api/v1/airports/search";

/**
 * Query backend airport autocomplete.
 * @param {string} query - user query (min 2-3 chars)
 * @param {number} [limit=10] - number of items to return
 * @param {AbortSignal} [signal] - optional abort signal
 * @returns {Promise<Array>} airports array
 */
export const autocompleteAirports = async (query, limit = 10, signal) => {
  if (!query) return [];
  const response = await axios.get(API_URL, {
    params: { q: query, limit },
    signal,
  });
  return response.data;
};
