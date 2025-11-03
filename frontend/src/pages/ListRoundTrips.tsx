import { useSearchParams, useNavigate } from "react-router-dom";

export default function ListRoundTrips() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const origin = searchParams.get("origin") || "";
  const destination = searchParams.get("destination") || "";
  const departureDate = searchParams.get("departureDate") || "";
  const returnDate = searchParams.get("returnDate") || "";

  return (
    <div className="p-8">
      <h1 className="text-2xl font-semibold mb-4">Round Trip Flights (Fallback)</h1>
      <p className="mb-4">Round-trip searches are not fully supported in this prototype yet.</p>
      <div className="mb-4 text-sm text-gray-700">
        <div>Origin: {origin}</div>
        <div>Destination: {destination}</div>
        <div>Departure: {departureDate}</div>
        <div>Return: {returnDate}</div>
      </div>
      <div className="space-x-3">
        <button className="px-4 py-2 bg-blue-600 text-white rounded" onClick={() => navigate('/search')}>Back to Search</button>
        <button className="px-4 py-2 border rounded" onClick={() => navigate(-1)}>Go Back</button>
      </div>
    </div>
  );
}
