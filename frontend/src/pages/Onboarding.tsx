import { useNavigate } from "react-router-dom";
import onBg from "../assets/imgs/onBg.svg";

export default function Onboarding() {
  const navigate = useNavigate();

  return (
    <div
      style={{ backgroundImage: `url(${onBg})` }}
      className="min-h-screen w-full md:bg-cover bg-no-repeat bg-bottom bg-white flex flex-col "
    >
      <section className="flex-none min-h-[85vh] w-full flex flex-col items-center justify-center px-6 text-center">
        <h1 className="text-blue-dark text-4xl sm:text-5xl md:text-6xl ">
          Check the flights you need
        </h1>
        <p className="text-blue-light text-4xl sm:text-5xl md:text-6xl m-3">
          Whenever you need
        </p>
        <button
          className="bg-blue-dark text-white text-xl sm:text-2xl px-8 py-4 rounded-2xl mt-10
                     hover:bg-blue-light transition-colors"
          onClick={() => navigate("/search")}
        >
          Get Started
        </button>
      </section>
    </div>
  );
}
