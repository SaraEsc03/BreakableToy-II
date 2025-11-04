import { useNavigate } from "react-router-dom";
import listBg from "../assets/imgs/listBg.svg";
import { formatDateTime, formatDateShort } from "../utils/formatters/date";
import { formatAmount } from "../utils/formatters/number";
import type { TravelerPricing, PriceTotals } from "../types/flight";
import useOffer from "../hooks/useOffer";
import { representativeFareForSegment } from "../utils/representativeFare";
import { flattenSegments, isOperatingDifferent } from "../utils/segmentUtils";

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
							const fare = representativeFareForSegment(travelerPricings, seg.id);
							const operatingDifferent = isOperatingDifferent(seg);
							return (
								<article key={`segment-${seg.id ?? i}`} className="border-yellow-sun border rounded-lg p-4">
									<div className="flex items-start justify-between">
										<div>
											<div className="text-sm text-gray-500">{formatDateShort(seg.departureDateTime)}</div>
											<div className="font-semibold text-lg text-blue-dark">{formatDateTime(seg.departureDateTime)} — {formatDateTime(seg.arrivalDateTime)}</div>
											<div className="text-sm text-gray-700 mt-1">{seg.departureAirport?.name ?? ''} ({seg.departureAirport?.code ?? ''}) → {seg.arrivalAirport?.name ?? ''} ({seg.arrivalAirport?.code ?? ''})</div>
										</div>
										<div className="text-right">
											<div className="font-medium">{seg.airline?.code ?? ''} · {seg.airline?.name ?? ''}</div>
											<div className="text-sm text-gray-600">{seg.flightNumber ? `Flight ${seg.flightNumber}` : ''}</div>
											<div className="text-xs text-gray-500">Aircraft {seg.aircraftType ?? ''}</div>
										</div>
									</div>

									{operatingDifferent ? (
										<div className="mt-3 text-sm text-gray-600">Operated by: {seg.operatingAirline?.code} · {seg.operatingAirline?.name}</div>
									) : null}

									<div className="mt-4">
										<div className="font-semibold text-lg text-blue-dark">Fare Details</div>
										{fare ? (
											<div className="mt-2 text-sm text-gray-700">
												<div>Cabin: {fare.cabin ?? '—'}</div>
												<div>Class: {fare.classTrip ?? '—'}</div>
												<div className="mt-2 space-y-2">
													{(fare.amenities ?? []).map((a, idx) => (
														<div key={idx}>
															<div className="font-medium">{a.description}</div>
															<div className="text-xs text-gray-500">{a.isChargeable ? '(chargeable)' : '(not chargeable)'}</div>
														</div>
													))}
												</div>
											</div>
										) : (
											<div className="text-sm text-gray-500 mt-2">No fare details available for this segment</div>
										)}
									</div>

									{seg.nextLayover ? <div className="mt-3 text-sm text-gray-500">Layover: {seg.nextLayover}</div> : null}
								</article>
							);
						})}
					</div>
				</div>

				<aside className="col-span-1">
					<div className="bg-white  rounded-2xl p-6 shadow-md">
						<div className="font-bold text-3xl text-blue-dark">PRICE BREAKDOWN</div>
						<div className="mt-4 text-sm text-gray-700">Base: {priceTotals?.currency ? `${priceTotals.currency} ${formatAmount(priceTotals.base)}` : '-'}</div>
						<div className="text-sm font-semibold text-gray-700 mt-2">Fees</div>
						<div className="text-sm text-gray-600 mt-1">
							{priceTotals?.fees && priceTotals.fees.length > 0 ? (
								priceTotals.fees.map((f, idx) => (
									<div key={idx}>{f.type}: {priceTotals?.currency ?? ''} {formatAmount(f.amount)}</div>
								))
							) : (
								<div className="text-sm text-gray-500">No additional fees</div>
							)}
						</div>

						<div className="mt-4 font-bold text-lg">Total: {priceTotals?.currency ? `${priceTotals.currency} ${formatAmount(priceTotals.grandTotal ?? priceTotals.total)}` : '-'}</div>

						<div className="mt-6 border rounded-lg p-4 border-yellow-sun">
							<div className="font-semibold">Per Traveler</div>
							<div className="mt-2 text-sm text-gray-700">
								{travelerPricings.length > 0 ? (
									travelerPricings.map((tp, idx) => (
										<div key={idx} className="mb-3">
											<div className="font-medium">Traveler {idx + 1}</div>
											<div className="text-sm">Price: {tp.priceTravelerDetails?.currency ? `${tp.priceTravelerDetails.currency} ${formatAmount(tp.priceTravelerDetails.total)}` : '—'}</div>
										</div>
									))
								) : (
									<div className="text-sm text-gray-500">No traveler pricing</div>
								)}
							</div>
						</div>
					</div>
				</aside>
			</div>
		</div>
	);
}
