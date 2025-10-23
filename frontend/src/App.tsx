import { BrowserRouter, Routes, Route } from "react-router-dom";
import SearchView from "./pages/FlightSearchView";
import FlightListView from "./pages/FlightListView";
import FlightDetailsView from "./pages/FlightDetailView";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<SearchView />} />
        <Route path="/flights" element={<FlightListView />} />
        <Route path="/details/:id" element={<FlightDetailsView />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
