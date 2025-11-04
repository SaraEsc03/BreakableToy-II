import { useState } from "react";
import searchBg from "../assets/imgs/searchBg.svg";
import SearchForm from "../components/search/SearchForm";
import useSearchForm from "../hooks/useSearchForm";
import useSubmitSearch from "../hooks/useSubmitSearch";

export default function SearchView() {
  
  const todayIso = new Date().toISOString().split("T")[0];
  const [error, setError] = useState("");

  const { values, handleChange, handleSelectAirport, validate } = useSearchForm();
  const { submit, loading, error: submitError } = useSubmitSearch();

  // handleChange and handleSelectAirport come from useSearchForm

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    const validation = validate();
    if (Object.keys(validation).length > 0) {
      setError(Object.values(validation)[0] as string);
      return;
    }

    try {
      await submit(values);
    } catch {
      setError(submitError ?? "Failed to search flights. Please try again.");
    }
  };

  return (
    <div
      style={{ backgroundImage: `url(${searchBg})` }}
      className="min-h-screen w-full bg-cover bg-no-repeat bg-bottom bg-white flex flex-col items-center justify-center px-4 py-8"
    >
		
      <div className="w-full max-w-4xl">
        {/* Header */}
        

        <SearchForm
          values={values}
          onChange={handleChange}
          onSelectAirport={handleSelectAirport}
          onSubmit={handleSubmit}
          loading={loading}
          error={error || (submitError ?? undefined)}
          todayIso={todayIso}
        />
      </div>
    </div>
  );
}
