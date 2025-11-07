import type { FareDetails } from "../../types/flight";

type Props = { fare?: FareDetails };

export default function FareDetailsPanel({ fare }: Props) {
  if (!fare) return <div className="text-sm text-gray-500 mt-2">No fare details available for this segment</div>;

  return (
    <div className="mt-2 text-sm text-gray-700">
      <div>Cabin: {fare.cabin ?? "—"}</div>
      <div>Class: {fare.classTrip ?? "—"}</div>
      <div className="mt-2 space-y-2">
        {(fare.amenities ?? []).map((a, idx) => (
          <div key={idx}>
            <div className="font-medium">{a.description}</div>
            <div className="text-xs text-gray-500">{a.isChargeable ? "(chargeable)" : "(not chargeable)"}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
