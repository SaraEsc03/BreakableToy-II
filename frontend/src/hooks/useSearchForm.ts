import { useState, useCallback } from "react";
import type { SearchFormValues, ValidationErrors } from "../types/search";

const defaultValues: SearchFormValues = {
  departureAirport: "",
  arrivalAirport: "",
  departureDate: "",
  returnDate: "",
  currency: "USD",
  nonStop: false,
  adults: 1,
};

export default function useSearchForm(initial?: Partial<SearchFormValues>) {
  const [values, setValues] = useState<SearchFormValues>({ ...defaultValues, ...initial });

  const setValue = useCallback(<K extends keyof SearchFormValues>(key: K, value: SearchFormValues[K]) => {
    setValues((v) => ({ ...v, [key]: value }));
  }, []);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const target = e.target as HTMLInputElement | HTMLSelectElement;
    const { name } = target;
    if (!name) return;

    if ((target as HTMLInputElement).type === "checkbox") {
      const checked = (target as HTMLInputElement).checked;
      setValues((v) => ({ ...v, [name]: checked } as unknown as SearchFormValues));
      return;
    }

    const value = (target as HTMLInputElement).value;
    setValues((v) => ({ ...v, [name]: name === "adults" ? Number(value) : value } as unknown as SearchFormValues));
  }, []);

  const handleSelectAirport = useCallback((field: "departureAirport" | "arrivalAirport", airport?: { code?: string }) => {
    if (!airport || !airport.code) return;
    setValues((v) => ({ ...v, [field]: airport.code } as SearchFormValues));
  }, []);

  const validate = useCallback((vals: SearchFormValues = values): ValidationErrors => {
    const errors: ValidationErrors = {};
    if (!vals.departureAirport) errors.departureAirport = "Departure airport is required";
    if (!vals.arrivalAirport) errors.arrivalAirport = "Arrival airport is required";
    if (!vals.departureDate) errors.departureDate = "Departure date is required";
    if (!vals.adults || vals.adults < 1) errors.adults = "At least one adult required";
    if (vals.returnDate && vals.departureDate && vals.returnDate < vals.departureDate)
      errors.returnDate = "Return date cannot be before departure date";
    return errors;
  }, [values]);

  const reset = useCallback((next?: Partial<SearchFormValues>) => setValues({ ...defaultValues, ...next }), []);

  return { values, setValues, setValue, handleChange, handleSelectAirport, validate, reset } as const;
}
