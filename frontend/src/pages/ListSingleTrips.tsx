import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import useFlightResults from "../hooks/useFlightResults";
import listBg from "../assets/imgs/listBg.svg";
import type { FlightOffer } from "../types/flight";
import TripCard from "../components/lists/TripCard";

export default function ListSingleTrips() {
  const navigate = useNavigate();

  const { results, loading, error } = useFlightResults();

  const openOffer = useCallback((offer: FlightOffer) => () => {
    navigate(`/details/${offer.id}`, { state: { offer } });
  }, [navigate]);

  if (loading) return <div className="p-8">Loading flights…</div>;
  if (error) return <div className="p-8 text-red-600">{error}</div>;
  if (!results || !results.flightOffers || results.flightOffers.length === 0)
    return (
      <div className="p-8">
        <p>No flight offers found.</p>
        <button className="mt-4 px-4 py-2 bg-blue-600 text-white rounded" onClick={() => navigate('/search')}>{'< Return to Search'}</button>
      </div>
    );

  return (
  <div style={{ backgroundImage: `url(${listBg})`, backgroundAttachment: 'fixed' }} className="p-8 w-full bg-cover bg-no-repeat bg-bottom bg-fixed bg-gray-50 min-h-screen">
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <button onClick={() => navigate('/search')} className="px-4 py-2 bg-blue-dark text-white rounded-md shadow hover:bg-blue-light transition-colors">&lt; Return to Search</button>
        
        </div>

        <div className="space-y-6">
  {results.flightOffers.map((offer: FlightOffer) => (
    <TripCard key={offer.id} offer={offer} onOpen={openOffer(offer)} variant="single" />
  ))}
      </div>
      </div>
    </div>
  );
}
