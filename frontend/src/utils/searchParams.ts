import { toIsoDate } from "./formatters/date";

export type SearchApiParams = {
  origin: string;
  destination: string;
  departureDate: string;
  returnDate?: string;
  currencyCode?: string;
  nonStop?: boolean;
  adults?: number;
};

export type ParsedSearch = {
  mode: "none" | "single" | "round";
  apiParams?: SearchApiParams;
  raw?: URLSearchParams;
};

function normalizeDate(raw?: string | null) {
  if (!raw) return undefined;
  // accept mm/dd/yyyy or yyyy-mm-dd
  return raw.includes("/") ? toIsoDate(raw) : raw;
}

export function parseSearchParams(searchString: string): ParsedSearch {
  const params = new URLSearchParams(searchString);
  const origin = params.get("origin");
  const destination = params.get("destination");
  const departureDateRaw = params.get("departureDate");
  if (!origin || !destination || !departureDateRaw) return { mode: "none" };

  const departureDate = normalizeDate(departureDateRaw)!;
  const returnDateRaw = params.get("returnDate");
  const returnDate = returnDateRaw ? normalizeDate(returnDateRaw) : undefined;

  const apiParams: SearchApiParams = {
    origin,
    destination,
    departureDate,
    returnDate: returnDate ?? undefined,
    currencyCode: params.get("currencyCode") || undefined,
    nonStop: params.get("nonStop") === "true" ? true : undefined,
    adults: params.get("adults") ? Number(params.get("adults")) : undefined,
  };

  return { mode: returnDate ? "round" : "single", apiParams, raw: params };
}

export function buildDeepLinkParams(p: SearchApiParams): URLSearchParams {
  const params = new URLSearchParams();
  params.set("origin", p.origin);
  params.set("destination", p.destination);
  // keep dates in mm/dd/yyyy for compatibility with current UI where needed
  const dep = p.departureDate;
  if (dep) {
    const [y, m, d] = dep.split("-");
    params.set("departureDate", `${m}/${d}/${y}`);
  }
  if (p.returnDate) {
    const [y, m, d] = p.returnDate.split("-");
    params.set("returnDate", `${m}/${d}/${y}`);
  }
  if (p.currencyCode) params.set("currencyCode", p.currencyCode);
  if (p.nonStop) params.set("nonStop", String(p.nonStop));
  if (p.adults) params.set("adults", String(p.adults));
  return params;
}
