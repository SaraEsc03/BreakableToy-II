import axios from "axios";

// Central axios instance for the frontend. Keeps base config and interceptors in one place.
const instance = axios.create({
  timeout: 15000,
});

// Optional: add response / error interceptors here if needed (logging, auth, etc.)
// instance.interceptors.response.use(response => response, err => { ... });

/**
 * GET wrapper that accepts an AbortSignal (signal) and params.
 * url may be a full URL or a path. Returns the response.data.
 */
export async function get(url, { params, signal } = {}) {
  const config = { params };
  if (signal) config.signal = signal;
  const resp = await instance.get(url, config);
  return resp.data;
}

export default { get };
