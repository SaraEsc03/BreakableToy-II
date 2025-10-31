import axios from "axios";

const API_URL = "http://localhost:8000/api/flights"; // adjust if needed

export const searchFlights = async (params) => {
  const response = await axios.get(`${API_URL}/search`, { params });
  return response.data;
};
