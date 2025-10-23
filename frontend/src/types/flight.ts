export interface Flight {
  id: string;
  airline?: string;
  origin?: string;
  destination?: string;
  departureTime?: string; // ISO datetime
  arrivalTime?: string; // ISO datetime
  price?: number;
  // Add other fields as needed based on API response
}
