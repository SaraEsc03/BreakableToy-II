import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import { AnimatePresence } from "framer-motion";
import Onboarding from "../pages/Onboarding";
import SearchView from "../pages/SearchView";
import ListSingleTrips from "../pages/ListSingleTrips";
import ListRoundTrips from "../pages/ListRoundTrips";
import DetailsSegment from "../pages/DetailsSegment";
import PageTransition from "../components/PageTransition";

function AnimatedRoutes() {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<PageTransition><Onboarding /></PageTransition>} />
        <Route path="/search" element={<PageTransition><SearchView /></PageTransition>} />
        <Route path="/single-trips" element={<PageTransition><ListSingleTrips /></PageTransition>} />
        <Route path="/round-trips" element={<PageTransition><ListRoundTrips /></PageTransition>} />
        <Route path="/details/:id" element={<PageTransition><DetailsSegment /></PageTransition>} />
      </Routes>
    </AnimatePresence>
  );
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <AnimatedRoutes />
    </BrowserRouter>
  );
}
