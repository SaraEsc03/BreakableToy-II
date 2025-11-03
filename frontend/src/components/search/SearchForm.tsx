import React from "react";
import AirportAutocomplete from "./AirportAutocomplete";
import type { SearchFormValues } from "../../types/search";

interface Props {
  values: SearchFormValues;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
  onSelectAirport: (field: "departureAirport" | "arrivalAirport", airport?: { code?: string; name?: string }) => void;
  onSubmit: (e: React.FormEvent) => void;
  loading?: boolean;
  error?: string;
  todayIso?: string;
}

export default function SearchForm({ values, onChange, onSelectAirport, onSubmit, loading = false, error, todayIso }: Props) {
  return (
    <div className="bg-white rounded-3xl shadow-lg border border-gray-200 p-8 mb-8">
      <h1 className="text-blue-dark text-3xl md:text-4xl font-medium mb-8 text-center">
        Where and how do you wanna fly?
      </h1>

      <form onSubmit={onSubmit}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div>
            <AirportAutocomplete
              label="Departure Airport"
              required
              placeholder="Type a city or code (e.g., JFK, New York)"
              onSelect={(a) => onSelectAirport("departureAirport", a)}
            />
          </div>

          <div>
            <AirportAutocomplete
              label="Arrival Airport"
              required
              placeholder="Type a city or code (e.g., LAX, Los Angeles)"
              onSelect={(a) => onSelectAirport("arrivalAirport", a)}
            />
          </div>

          <div>
            <label className="block text-blue-dark text-lg font-medium mb-2">
              Departure Date <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              name="departureDate"
              value={values.departureDate}
              onChange={onChange}
              min={todayIso}
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-blue-dark text-lg font-medium mb-2">Return Date</label>
            <input
              type="date"
              name="returnDate"
              value={values.returnDate}
              onChange={onChange}
              min={values.departureDate || todayIso}
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-blue-dark text-lg font-medium mb-2">Currency</label>
            <select
              name="currency"
              value={values.currency}
              onChange={onChange}
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
            >
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
              <option value="GBP">GBP</option>
              <option value="MXN">MXN</option>
            </select>
          </div>

          <div>
            <label className="block text-blue-dark text-lg font-medium mb-2">Adults <span className="text-red-500">*</span></label>
            <input
              type="number"
              name="adults"
              min={1}
              max={9}
              value={values.adults}
              onChange={onChange}
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
              required
            />
          </div>

          <div className="flex items-end">
            <label className="flex items-center space-x-3 cursor-pointer">
              <input
                type="checkbox"
                name="nonStop"
                checked={values.nonStop}
                onChange={onChange}
                className="w-5 h-5 rounded border-gray-300 text-blue-dark focus:ring-2 focus:ring-blue-light"
              />
              <span className="text-blue-dark text-lg font-medium">Non Stop?</span>
            </label>
          </div>
        </div>

        {error && (
          <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-xl text-red-600 text-center">
            {error}
          </div>
        )}

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-dark text-white text-xl font-medium py-4 rounded-full hover:bg-blue-light transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? "SEARCHING..." : "START SEARCHING"}
        </button>
      </form>
    </div>
  );
}
