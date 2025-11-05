import React from "react";
import { formatAmount } from "../../utils/formatters/number";
import type { PriceTotals, TravelerPricing } from "../../types/flight";

type Props = { priceTotals?: PriceTotals; travelerPricings?: TravelerPricing[] };

export default function PriceBreakdown({ priceTotals, travelerPricings = [] }: Props) {
  return (
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
  );
}
