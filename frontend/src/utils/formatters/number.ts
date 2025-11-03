export function formatAmount(amount?: string) {
  if (!amount) return "";
  // remove existing commas, parse number
  const n = Number(String(amount).replace(/,/g, ""));
  if (Number.isNaN(n)) return amount;
  return n.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
