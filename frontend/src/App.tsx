import AppRouter from "./router/AppRouter";
import { FlightsProvider } from "./context/FlightsContext";

function App() {
  return (
    <FlightsProvider>
      <AppRouter />
    </FlightsProvider>
  );
}

export default App;
