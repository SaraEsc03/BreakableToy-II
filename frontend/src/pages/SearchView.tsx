import { useState } from "react";
import { useNavigate } from "react-router-dom";
import searchBg from "../assets/imgs/searchBg.svg";
import { searchFlights } from "../api/flightsApi";
import AirportAutocomplete from "../components/AirportAutocomplete";

export default function SearchView() {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    departureAirport: "",
    arrivalAirport: "",
    departureDate: "",
    returnDate: "",
    currency: "USD",
    nonStop: false,
    adults: 1,
  });

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  // Autocomplete input values
  const [originInput, _setOriginInput] = useState("");
  const [destinationInput, _setDestinationInput] = useState("");

  // When user types, clear the stored code so submit requires a fresh selection
  const setOriginInput = (v: string) => {
    _setOriginInput(v);
    setFormData((prev) => ({ ...prev, departureAirport: "" }));
  };
  const setDestinationInput = (v: string) => {
    _setDestinationInput(v);
    setFormData((prev) => ({ ...prev, arrivalAirport: "" }));
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;
    
    setFormData(prev => ({
      ...prev,
      [name]: type === "checkbox" ? checked : (name === "adults" ? Number(value) : value)
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    // Validation
    if (!formData.departureAirport || !formData.arrivalAirport || !formData.departureDate) {
      setError("Please fill in all required fields");
      return;
    }
    if (!formData.adults || formData.adults < 1) {
      setError("Adults must be at least 1");
      return;
    }

    setIsLoading(true);
    
    try {
      // Map UI fields to backend DTO parameter names
      const params = {
        origin: formData.departureAirport,
        destination: formData.arrivalAirport,
        departureDate: formData.departureDate,
        returnDate: formData.returnDate || undefined,
        currencyCode: formData.currency,
        nonStop: formData.nonStop,
        adults: formData.adults,
      };

      const results = await searchFlights(params);
      // Navigate to results page or handle the response
      navigate("/single-trips", { state: { flights: results } });
    } catch (err) {
      setError("Failed to search flights. Please try again.");
      console.error("Flight search error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      style={{ backgroundImage: `url(${searchBg})` }}
      className="min-h-screen w-full bg-cover bg-no-repeat bg-bottom bg-white flex flex-col items-center justify-center px-4 py-8"
    >
		<div className="text-right mb-6">
          <p className="text-blue-light text-md">Encora Spark | Sara Escamilla</p>
        </div>
      <div className="w-full max-w-4xl">
        {/* Header */}
        

        {/* Search Form Card */}
        <div className="bg-white rounded-3xl shadow-lg border border-gray-200 p-8 mb-8">
          <h1 className="text-blue-dark text-3xl md:text-4xl font-medium mb-8 text-center">
            Where and how do you wanna fly?
          </h1>

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
              {/* Departure Airport (Autocomplete) */}
              <div>
                <AirportAutocomplete
                  label="Departure Airport"
                  required
                  placeholder="Type a city or code (e.g., JFK, New York)"
                  inputValue={originInput}
                  setInputValue={setOriginInput}
                  onSelect={(a) => {
                    setFormData((prev) => ({ ...prev, departureAirport: a.code }));
                    setOriginInput(`${a.code} — ${a.name}${a.city ? ", " + a.city : ""}${a.country ? " (" + a.country + ")" : ""}`);
                  }}
                />
              </div>

              {/* Arrival Airport (Autocomplete) */}
              <div>
                <AirportAutocomplete
                  label="Arrival Airport"
                  required
                  placeholder="Type a city or code (e.g., LAX, Los Angeles)"
                  inputValue={destinationInput}
                  setInputValue={setDestinationInput}
                  onSelect={(a) => {
                    setFormData((prev) => ({ ...prev, arrivalAirport: a.code }));
                    setDestinationInput(`${a.code} — ${a.name}${a.city ? ", " + a.city : ""}${a.country ? " (" + a.country + ")" : ""}`);
                  }}
                />
              </div>

              {/* Departure Date */}
              <div>
                <label className="block text-blue-dark text-lg font-medium mb-2">
                  Departure Date <span className="text-red-500">*</span>
                </label>
                <input
                  type="date"
                  name="departureDate"
                  value={formData.departureDate}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
                  required
                />
              </div>

              {/* Return Date */}
              <div>
                <label className="block text-blue-dark text-lg font-medium mb-2">
                  Return Date
                </label>
                <input
                  type="date"
                  name="returnDate"
                  value={formData.returnDate}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
                />
              </div>

              {/* Currency */}
              <div>
                <label className="block text-blue-dark text-lg font-medium mb-2">
                  Currency
                </label>
                <select
                  name="currency"
                  value={formData.currency}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
                >
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                  <option value="GBP">GBP</option>
                  <option value="MXN">MXN</option>
                </select>
              </div>

              {/* Adults */}
              <div>
                <label className="block text-blue-dark text-lg font-medium mb-2">
                  Adults <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  name="adults"
                  min={1}
                  max={9}
                  value={formData.adults}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
                  required
                />
              </div>

              {/* Non Stop */}
              <div className="flex items-end">
                <label className="flex items-center space-x-3 cursor-pointer">
                  <input
                    type="checkbox"
                    name="nonStop"
                    checked={formData.nonStop}
                    onChange={handleInputChange}
                    className="w-5 h-5 rounded border-gray-300 text-blue-dark focus:ring-2 focus:ring-blue-light"
                  />
                  <span className="text-blue-dark text-lg font-medium">Non Stop?</span>
                </label>
              </div>
            </div>

            {/* Error Message */}
            {error && (
              <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-xl text-red-600 text-center">
                {error}
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-blue-dark text-white text-xl font-medium py-4 rounded-full hover:bg-blue-light transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? "SEARCHING..." : "START SEARCHING"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
