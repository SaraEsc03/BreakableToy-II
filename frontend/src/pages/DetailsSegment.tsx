import { useNavigate } from "react-router-dom";
import listBg from "../assets/imgs/listBg.svg";
// date formatting handled inside detail components
import type { TravelerPricing, PriceTotals } from "../types/flight";
import useOffer from "../hooks/useOffer";
import { flattenSegments } from "../utils/segmentUtils";
import SegmentCard from "../components/details/SegmentCard";
import PriceBreakdown from "../components/details/PriceBreakdown";

export default function DetailsSegment() {
	const navigate = useNavigate();
	const { offer } = useOffer();

	// derive lightweight values and call hooks unconditionally
	const priceTotals: PriceTotals | undefined = offer?.priceTotals;
	const travelerPricings: TravelerPricing[] = offer?.travelerPricings ?? [];
	const segments = flattenSegments(offer);

	if (!offer) {
		return (
			<div className="p-8">
				<button onClick={() => navigate(-1)} className="px-4 py-2 bg-blue-dark text-white rounded-md hover:bg-blue-light transition-colors">&lt; Return to Flights</button>
				<div className="mt-6">Could not find flight details for this selection.</div>
			</div>
		);
	}

	// Layover now provided by backend in segment.nextLayover (human) and nextLayoverIso (ISO)

	return (
		<div style={{ backgroundImage: `url(${listBg})`, backgroundAttachment: 'fixed' }} className="p-8 w-full bg-cover bg-no-repeat bg-bottom bg-fixed bg-gray-50 min-h-screen">
			<div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-6">
				<div className="col-span-2 bg-white/40 rounded-2xl p-6 backdrop-blur-sm shadow-xl">
					<div className="mb-4">
						<button onClick={() => navigate(-1)} className="px-4 py-2 bg-blue-dark text-white rounded-md">&lt; Return to Flights</button>
					</div>

					<div className="space-y-6">
						{segments.map((entry, i) => {
							const { seg } = entry;
							return <SegmentCard key={`segment-${seg.id ?? i}`} seg={seg} travelerPricings={travelerPricings} index={i} />;
						})}
					</div>
				</div>

				<aside className="col-span-1">
					<PriceBreakdown priceTotals={priceTotals} travelerPricings={travelerPricings} />
				</aside>
			</div>
		</div>
	);
}
