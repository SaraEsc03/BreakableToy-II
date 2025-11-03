export function toIsoDate(mmddyyyy: string | null): string | null {
  if (!mmddyyyy) return mmddyyyy;
  const parts = mmddyyyy.split("/");
  if (parts.length !== 3) return mmddyyyy;
  const [mm, dd, yyyy] = parts;
  return `${yyyy}-${mm.padStart(2, "0")}-${dd.padStart(2, "0")}`;
}

export function formatTimeShort(iso?: string) {
  if (!iso) return "";
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleTimeString(undefined, { hour: "numeric", minute: "2-digit" });
}
