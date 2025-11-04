import { useMemo } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import listBg from "../assets/imgs/listBg.svg";
import { formatTimeShort, formatDateShort, formatDateTime } from "../utils/formatters/date";
import { formatAmount } from "../utils/formatters/number";
import { useFlights } from "../context/FlightsContext";
import { getStopsSummary } from "../utils/flightSelectors";
import type { FlightOffer, Itinerary, Segment, StopInfo } from "../types/flight";

export default function DetailsSegment() {
	const navigate = useNavigate();
	const { id } = useParams();
	const location = useLocation();
	const { flights } = useFlights();

	// Offer may come from location.state (when navigation passes it) or from context (array of offers)
		const offer: FlightOffer | undefined = useMemo(() => {
			// location.state may contain either an `offer` or a `flights` result
				const state = (location as unknown as { state?: unknown }).state as unknown as { offer?: FlightOffer; flights?: unknown } | undefined;
				if (state?.offer) return state.offer;
				if (state?.flights) {
					const maybe = state.flights;
				if (Array.isArray(maybe)) return maybe.find((o: FlightOffer) => String(o.id) === String(id));
				const maybeObj = maybe as { flightOffers?: FlightOffer[] };
				if (maybeObj.flightOffers && Array.isArray(maybeObj.flightOffers)) return maybeObj.flightOffers.find((o: FlightOffer) => String(o.id) === String(id));
			}

			if (Array.isArray(flights)) return flights.find((f: FlightOffer) => String(f.id) === String(id));
			return undefined;
		}, [location, flights, id]);

	if (!offer) {
		return (
			<div className="p-8">
				<button onClick={() => navigate(-1)} className="px-4 py-2 bg-blue-dark text-white rounded-md">&lt; Return to Flights</button>
				<div className="mt-6">Could not find flight details for this selection.</div>
			</div>
		);
	}

	// For display: show outbound (itinerary 0) and inbound (itinerary 1) when present
		const outbound: Itinerary | undefined = offer.itineraries?.[0];
		const inbound: Itinerary | undefined = offer.itineraries?.[1];

	const outboundSegs: Segment[] = outbound?.segments ?? [];
	const inboundSegs: Segment[] = inbound?.segments ?? [];

	// Price breakdown
	const priceTotals = offer.priceTotals;
	const travelerPricings = offer.travelerPricings ?? [];

		// Local helper types for unknown shapes in DTO
		type FareDetails = { segmentId?: string; cabin?: string; classTrip?: string; amenities?: { description?: string; isChargeable?: boolean }[] };

		// Helper to render stops details from itinerary
	function renderStops(itin?: Itinerary, segs: Segment[] = []) {
		const summary = getStopsSummary(itin);
		if (summary.count === 0) return <div className="text-sm text-gray-600">Non-stop</div>;

		return (
			<div className="space-y-3">
				{summary.details.map((s: StopInfo, idx: number) => {
					const nextArrival = segs[idx + 1]?.arrivalAirport;
					return (
							<div key={idx} className="flex flex-col items-start">
							<div className="text-sm font-medium">{s.duration}</div>
							<div className="text-sm text-gray-600 w-full">
								{s.airport?.name ?? ''} ({s.airport?.code ?? ''}){nextArrival ? ` - ${nextArrival.name} (${nextArrival.code})` : ''}
							</div>
														</div>
					);
				})}
			</div>
		);
	}

	return (
		<div style={{ backgroundImage: `url(${listBg})`, backgroundAttachment: 'fixed' }} className="p-8 w-full bg-cover bg-no-repeat bg-bottom bg-fixed bg-gray-50 min-h-screen">
			<div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-6">
				<div className="col-span-2 bg-white/40 rounded-2xl p-6 backdrop-blur-sm shadow-xl">
					<div className="mb-4">
						<button onClick={() => navigate(-1)} className="px-4 py-2 bg-blue-dark text-white rounded-md">&lt; Return to Flights</button>
					</div>

					<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
						<div>
							  <div className="text-sm text-gray-500">{formatDateShort(outbound?.initialDepartureDateTime)}</div>
							  <div className="font-semibold text-lg text-blue-dark">{formatTimeShort(outbound?.initialDepartureDateTime)} - {formatTimeShort(outbound?.finalArrivalDateTime)}</div>
							  <div className="text-sm text-gray-700 mt-1">{outboundSegs[0]?.departureAirport?.name ?? ''} ({outboundSegs[0]?.departureAirport?.code ?? ''}) - {outboundSegs[outboundSegs.length - 1]?.arrivalAirport?.name ?? ''} ({outboundSegs[outboundSegs.length - 1]?.arrivalAirport?.code ?? ''})</div>
							  <div className="text-sm text-gray-600 mt-3">{outbound?.totalDuration ?? ''}</div>
							<div className="text-sm text-gray-700 mt-6">{outboundSegs[0]?.airline ? <span className="text-blue-dark">{outboundSegs[0].airline.name} ({outboundSegs[0].airline.code})</span> : ''}</div>

							<div className="mt-6">
								<div className="text-sm font-medium text-gray-800">Stops</div>
								<div className="mt-2">{renderStops(outbound, outboundSegs)}</div>
							</div>
						</div>

						<div>
							  <div className="text-sm text-gray-500">{inbound ? formatDateShort(inbound.initialDepartureDateTime) : ''}</div>
							  <div className="font-semibold text-lg text-blue-dark">Return: {inbound ? `${formatTimeShort(inbound.initialDepartureDateTime)} - ${formatTimeShort(inbound.finalArrivalDateTime)}` : '—'}</div>
							{inbound ? (
								<>
									<div className="text-sm text-gray-700 mt-1">{inboundSegs[0]?.departureAirport?.name ?? ''} ({inboundSegs[0]?.departureAirport?.code ?? ''}) - {inboundSegs[inboundSegs.length - 1]?.arrivalAirport?.name ?? ''} ({inboundSegs[inboundSegs.length - 1]?.arrivalAirport?.code ?? ''})</div>
									<div className="text-sm text-gray-600 mt-3">{inbound?.totalDuration ?? ''}</div>
									<div className="text-sm text-gray-700 mt-6">{inboundSegs[0]?.airline ? <span className="text-blue-dark">{inboundSegs[0].airline.name} ({inboundSegs[0].airline.code})</span> : ''}</div>
									<div className="mt-6">
										<div className="text-sm font-medium text-gray-800">Stops</div>
										<div className="mt-2">{renderStops(inbound, inboundSegs)}</div>
									</div>
								</>
							) : (
								<div className="text-sm text-gray-600 mt-2">No return itinerary</div>
							)}
						</div>
					</div>

					<div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
						{/* Fare details & amenities */}
						<div className="border rounded-lg p-4">
							<div className="font-semibold">Travelers Fare Details</div>
							{travelerPricings.length > 0 ? (
								travelerPricings.map((tp, idx) => {
									const fare = tp.fareDetailsBySegment as unknown as FareDetails | undefined;
									return (
										<div key={idx} className="mt-3 text-sm text-gray-700">
											<div>Cabin: {fare?.cabin ?? '—'}</div>
											<div>Class: {fare?.classTrip ?? '—'}</div>
										</div>
									);
								})
							) : (
								<div className="text-sm text-gray-600 mt-2">No traveler pricing details available</div>
							)}

							<div className="mt-4">
								<div className="font-semibold">Amenities</div>
								{(() => {
									const firstFare = travelerPricings.length > 0 ? (travelerPricings[0].fareDetailsBySegment as unknown as FareDetails | undefined) : undefined;
									const amenities = firstFare?.amenities;
									if (amenities && amenities.length > 0) {
										return (
											<div className="mt-2 space-y-2 text-sm text-gray-700">
												{amenities.map((a, i) => (
													<div key={i}>
														<div className="font-medium">{a.description}</div>
														<div className="text-xs text-gray-500">{a.isChargeable ? '(chargeable)' : '(not chargeable)'}</div>
													</div>
												))}
											</div>
										);
									}
									return <div className="text-sm text-gray-600 mt-2">No amenities information</div>;
								})()}
							</div>
						</div>

						{/* Placeholder for additional info or selected segment details */}
								<div className="space-y-4">
									{/* Render each segment as its own card, ordered by departure datetime */}
									{(() => {
										// Combine segments from outbound and inbound into one list preserving itinerary order
										const combined: Segment[] = [];
										if (outboundSegs.length > 0) combined.push(...outboundSegs);
										if (inboundSegs.length > 0) combined.push(...inboundSegs);
										// sort by departureDateTime to ensure chronological order
										combined.sort((a, b) => {
											const da = a.departureDateTime ? new Date(a.departureDateTime).getTime() : 0;
											const db = b.departureDateTime ? new Date(b.departureDateTime).getTime() : 0;
											return da - db;
										});

										// Helper: get fare details for a segment by matching segmentId
										function faresForSegment(segmentId?: string) {
											if (!segmentId) return [] as { travelerIndex: number; fare?: FareDetails }[];
											return travelerPricings.map((tp, idx) => {
												const fare = tp.fareDetailsBySegment as unknown as FareDetails | undefined;
												if (fare && String(fare.segmentId) === String(segmentId)) return { travelerIndex: idx, fare };
												return { travelerIndex: idx, fare: undefined };
											}).filter(x => x.fare !== undefined);
										}

										// Helper: format layover between this and next segment
										function layoverBetween(current: Segment, next?: Segment) {
											if (!next || !current || !current.arrivalDateTime || !next.departureDateTime) return null;
											const a = new Date(current.arrivalDateTime).getTime();
											const b = new Date(next.departureDateTime).getTime();
											const diff = b - a;
											if (isNaN(diff) || diff <= 0) return null;
											const mins = Math.floor(diff / 60000);
											const h = Math.floor(mins / 60);
											const m = mins % 60;
											return `${h > 0 ? `${h}h ` : ''}${m}m`;
										}

										return combined.map((seg, idx) => {
											const segExtra = seg as unknown as { id?: string; flightNumber?: string; aircraftType?: string; duration?: string };
											const fares = faresForSegment(segExtra.id);
											const next = combined[idx + 1];
											const layover = layoverBetween(seg, next);
											return (
												<div key={`seg-${idx}`} className="border rounded-lg p-4">
													<div className="font-medium text-blue-dark">{seg.airline?.name ?? seg.operatingAirline?.name} {seg.airline?.code ?? seg.operatingAirline?.code} {segExtra.flightNumber ? `· ${segExtra.flightNumber}` : ''}</div>
													<div className="text-xs text-gray-500">{formatDateTime(seg.departureDateTime)} → {formatDateTime(seg.arrivalDateTime)}{segExtra.aircraftType ? ` · ${segExtra.aircraftType}` : ''}</div>
													<div className="mt-3 text-sm text-gray-700">
														{/* Traveler fare details for this segment */}
														{fares.length > 0 ? (
															fares.map(f => (
																<div key={f.travelerIndex} className="mb-2">
																<div className="font-medium">Traveler {f.travelerIndex + 1}</div>
																<div className="text-sm">Cabin: {f.fare?.cabin ?? '—'}</div>
																<div className="text-sm">Class: {f.fare?.classTrip ?? '—'}</div>
																{/* amenities */}
																{f.fare?.amenities && f.fare.amenities.length > 0 ? (
																	<div className="mt-2 space-y-1 text-sm text-gray-700">
																	{f.fare.amenities.map((a, i) => (
																		<div key={i}>
																			<div className="font-medium">{a.description}</div>
																			<div className="text-xs text-gray-500">{a.isChargeable ? '(chargeable)' : '(not chargeable)'}</div>
																		</div>
																	))}
																	</div>
																) : null}
														</div>
														))
														) : (
														<div className="text-sm text-gray-500">No fare details for this segment</div>
														)}
													</div>
													{layover ? <div className="mt-3 text-sm text-gray-500">Layover: {layover}</div> : null}
												</div>
											);
										});
									})()}
								</div>
					</div>
				</div>

				{/* Price breakdown column */}
				<aside className="col-span-1">
					<div className="bg-white rounded-2xl p-6 shadow-md">
						<div className="font-semibold text-xl text-blue-dark">Price Breakdowns</div>
						<div className="mt-4 text-sm text-gray-700">Base: {priceTotals?.currency ? `${priceTotals.currency} ${formatAmount(priceTotals.base)}` : '-'}</div>
						<div className="text-sm text-gray-700 mt-2">Fees</div>
						<div className="text-sm text-gray-600 mt-1">
							{(() => {
								const fees = (priceTotals as unknown as { fees?: { amount?: string; type?: string }[] } )?.fees;
								if (fees && fees.length > 0) {
									return fees.map((f, idx) => (
										<div key={idx}>{f.type}: {priceTotals?.currency ?? ''} {formatAmount(f.amount)}</div>
									));
								}
								return <div className="text-sm text-gray-500">No additional fees</div>;
							})()}
						</div>

						<div className="mt-4 font-bold text-lg">Grand Total: {priceTotals?.currency ? `${priceTotals.currency} ${formatAmount(priceTotals.grandTotal ?? priceTotals.total)}` : '-'}</div>

						<div className="mt-6 border rounded-lg p-4">
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
