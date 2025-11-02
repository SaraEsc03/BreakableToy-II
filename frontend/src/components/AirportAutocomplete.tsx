import { useEffect, useId, useRef } from "react";
import type { Airport } from "../hooks/useAirportAutocomplete";
import { useAirportAutocomplete } from "../hooks/useAirportAutocomplete";

type Props = {
  label: string;
  required?: boolean;
  placeholder?: string;
  onSelect: (airport: Airport) => void;
  inputValue: string; // visible input value
  setInputValue: (v: string) => void;
};

export default function AirportAutocomplete({
  label,
  required,
  placeholder,
  onSelect,
  inputValue,
  setInputValue,
}: Props) {
  const listId = useId();
  const inputRef = useRef<HTMLInputElement | null>(null);

  const { results, loading, error, open, setOpen, highlightedIndex, moveHighlight } =
    useAirportAutocomplete({ minLength: 2, debounceMs: 300, limit: 10 });

  // Close the popup when clicking outside
  useEffect(() => {
    function onDocClick(e: MouseEvent) {
      if (!inputRef.current) return;
      const el = inputRef.current;
      if (!el.parentElement) return;
      if (!el.parentElement.contains(e.target as Node)) setOpen(false);
    }
    document.addEventListener("mousedown", onDocClick);
    return () => document.removeEventListener("mousedown", onDocClick);
  }, [setOpen]);

  const handleSelect = (airport: Airport) => {
    onSelect(airport);
    setOpen(false);
  };

  return (
    <div className="relative">
      <label className="block text-blue-dark text-lg font-medium mb-2">
        {label} {required && <span className="text-red-500">*</span>}
      </label>
      <input
        ref={inputRef}
        role="combobox"
        aria-expanded={open}
        aria-controls={listId}
        aria-autocomplete="list"
        type="text"
        value={inputValue}
        onFocus={() => setOpen(true)}
        onChange={(e) => setInputValue(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === "ArrowDown") {
            e.preventDefault();
            moveHighlight(1);
            setOpen(true);
          } else if (e.key === "ArrowUp") {
            e.preventDefault();
            moveHighlight(-1);
          } else if (e.key === "Enter") {
            if (open && highlightedIndex >= 0 && highlightedIndex < results.length) {
              e.preventDefault();
              handleSelect(results[highlightedIndex]);
            }
          } else if (e.key === "Escape") {
            setOpen(false);
          }
        }}
        placeholder={placeholder}
        className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-light focus:border-transparent"
      />

      {open && (
        <div
          id={listId}
          role="listbox"
          className="absolute z-20 mt-2 w-full bg-white border border-gray-200 rounded-xl shadow-lg max-h-64 overflow-auto"
        >
          {loading && (
            <div className="px-4 py-3 text-gray-500">Searching…</div>
          )}
          {!loading && error && (
            <div className="px-4 py-3 text-red-600">{error}</div>
          )}
          {!loading && !error && results.length === 0 && (
            <div className="px-4 py-3 text-gray-500">No airports found</div>
          )}
          {!loading && !error &&
            results.map((a, idx) => (
              <button
                type="button"
                key={`${a.code}-${idx}`}
                role="option"
                aria-selected={idx === highlightedIndex}
                onMouseDown={(e) => e.preventDefault()}
                onClick={() => handleSelect(a)}
                className={`w-full text-left px-4 py-2 hover:bg-gray-50 ${
                  idx === highlightedIndex ? "bg-gray-100" : ""
                }`}
              >
                <span className="font-semibold">{a.code}</span>
                <span className="text-gray-500"> — {a.name}</span>
                {a.city && <span className="text-gray-500">, {a.city}</span>}
                {a.country && <span className="text-gray-400"> ({a.country})</span>}
              </button>
            ))}
        </div>
      )}
    </div>
  );
}
