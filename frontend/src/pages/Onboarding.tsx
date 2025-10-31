import { useNavigate } from "react-router-dom";
import flightLogo from "../assets/imgs/flightLogo.png";

export default function Onboarding() {
  const navigate = useNavigate();

  return (
    <div className="bg-background h-screen w-screen flex flex-col overflow-hidden pt-20">
      {/* Text and button section - takes up space and centers content */}
      <div className="flex-1 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-blue-dark text-6xl m-3">
            Check the flights you need
          </h1>
          <p className="text-blue-light text-6xl m-3">
            Whenever you need
          </p>
          <button
            className="bg-blue-dark text-white text-2xl px-15 py-4 rounded-2xl mt-10 mx-auto
				 hover:bg-blue-light transition-colors"
				 onClick={() => navigate("/search")}
          >
            Get Started
          </button>
        </div>
      </div>
      
      {/* Image section - fixed at bottom */}
      <div className="relative h-64 flex justify-center overflow-hidden pl-24">
        <img
          src={flightLogo}
          alt="Flight Logo"
          className="absolute bottom-0 translate-y-60 h-150 w-auto object-contain"
        />
      </div>
	  
    </div>
  );
}
