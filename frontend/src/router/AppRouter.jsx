import { BrowserRouter, Routes, Route } from "react-router-dom";
import Onboarding from "../pages/Onboarding";
import SearchView from "../pages/SearchView";
import ListSingleTrips from "../pages/ListSingleTrips";
import ListRoundTrips from "../pages/ListRoundTrips";
import DetailsSegment from "../pages/DetailsSegment";

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Onboarding />} />
        <Route path="/search" element={<SearchView />} />
        <Route path="/single-trips" element={<ListSingleTrips />} />
        <Route path="/round-trips" element={<ListRoundTrips />} />
        <Route path="/details/:id" element={<DetailsSegment />} />
      </Routes>
    </BrowserRouter>
  );
}
