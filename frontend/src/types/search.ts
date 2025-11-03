export interface SearchFormValues {
  departureAirport: string;
  arrivalAirport: string;
  departureDate: string; // ISO yyyy-mm-dd
  returnDate?: string; // ISO yyyy-mm-dd or empty
  currency: string;
  nonStop: boolean;
  adults: number;
}

export interface SearchApiParams {
  origin: string;
  destination: string;
  departureDate: string;
  returnDate?: string;
  currencyCode?: string;
  nonStop?: boolean;
  adults?: number;
}

export type ValidationErrors = Partial<Record<keyof SearchFormValues, string>>;
