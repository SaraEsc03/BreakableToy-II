import { useState } from "react";
import { useNavigate } from "react-router-dom";
import searchBg from "../assets/imgs/searchBg.svg";
import { searchFlights } from "../api/flightsApi";
import AirportAutocomplete from "../components/AirportAutocomplete";

export default function SearchView() {
  const navigate = useNavigate();
  const todayIso = new Date().toISOString().split("T")[0];
  
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

    // If a return date is present, send the user to the Round Trips fallback for now
    if (formData.returnDate) {
      // Ensure returnDate is not before departureDate
      if (formData.departureDate && formData.returnDate < formData.departureDate) {
        setError("Return date cannot be before departure date");
        return;
      }

      const urlParams = new URLSearchParams();
      urlParams.set("origin", formData.departureAirport);
      urlParams.set("destination", formData.arrivalAirport);
      urlParams.set("departureDate", formData.departureDate);
      urlParams.set("returnDate", formData.returnDate);
      if (formData.currency) urlParams.set("currencyCode", formData.currency);
      if (formData.nonStop) urlParams.set("nonStop", String(formData.nonStop));
      if (formData.adults) urlParams.set("adults", String(formData.adults));

      navigate(`/round-trips?${urlParams.toString()}`);
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
      // For deep-linking, build a query string and navigate to /single-trips
      // The UI displays dates as mm/dd/yyyy in some places; format the date
      // to mm/dd/yyyy when placing it in the URL so the page can handle it.
      const toMmDdYyyy = (iso: string | undefined) => {
        if (!iso) return undefined;
        if (iso.includes("/")) return iso;
        // assume ISO yyyy-mm-dd -> convert
        const parts = iso.split("-");
        if (parts.length !== 3) return iso;
        const [y, m, d] = parts;
        return `${m}/${d}/${y}`;
      };

      const urlParams = new URLSearchParams();
      urlParams.set("origin", params.origin);
      urlParams.set("destination", params.destination);
      const dep = toMmDdYyyy(params.departureDate);
      if (dep) urlParams.set("departureDate", dep);
      const ret = params.returnDate ? toMmDdYyyy(params.returnDate) : undefined;
      if (ret) urlParams.set("returnDate", ret);
      if (params.currencyCode) urlParams.set("currencyCode", params.currencyCode);
      if (params.nonStop) urlParams.set("nonStop", String(params.nonStop));
      if (params.adults) urlParams.set("adults", String(params.adults));

      navigate(`/single-trips?${urlParams.toString()}`, { state: { flights: results } });
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
                  onSelect={(a) => setFormData((prev) => ({ ...prev, departureAirport: a.code }))}
                />
              </div>

              {/* Arrival Airport (Autocomplete) */}
              <div>
                <AirportAutocomplete
                  label="Arrival Airport"
                  required
                  placeholder="Type a city or code (e.g., LAX, Los Angeles)"
                  onSelect={(a) => setFormData((prev) => ({ ...prev, arrivalAirport: a.code }))}
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
                  min={todayIso}
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
                  min={formData.departureDate || todayIso}
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
