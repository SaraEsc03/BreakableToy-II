import { useParams } from "react-router-dom";

export default function FlightDetailView() {
	const { id } = useParams();
	return (
		<div>
			<h1>Flight Details</h1>
			<p>Details for flight id: {id}</p>
		</div>
	);
}
