import axios from "axios";

const API_URL = "http://localhost:8080/api/v1/flights"; // Spring Boot default port

export const searchFlights = async (params) => {
  const response = await axios.get(`${API_URL}/search`, { params });
  return response.data;
};
