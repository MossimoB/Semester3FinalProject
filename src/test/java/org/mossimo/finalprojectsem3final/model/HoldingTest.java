package org.mossimo.finalprojectsem3final.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HoldingTest {

    private static final double TOLERANCE = 0.001;

    private Stock stock() {
        return new Stock("NVT", "NovaTech", "Technology", "Test.", 100.00, 0.06, 0.002);
    }

    @Test
    @DisplayName("A new holding owns nothing and is worth nothing")
    void newHoling.getAverageCost(), TOLERANCE);
        assertEquals(0.0, holding.getCurrentValue(), TOLERANCE);
        assertEquals(0.0, holding.getProfitLoss(), TOLERANCE);

        // Must not divide by zero on an empty holding.
        assertEquals(0.0, holding.getReturnPercent(), TOLERANCE);
        assertTrue(holding.isEmpty());
    }

    @Test
    @DisplayName("One purchase sets the average cost to the price paid")

    }

    @Test
    @DisplayName("Average cost is WEIGHTED by share count, not a plain average")
    void twoPurchases_averageIsWeighted() {
        Holding holding = new Holding(stock());

        holding.addShares(10, 100.00);   // $1,000
        holding.addShares(30, 200.00);   // $6,000

        // Total $7,000 across 40 shares = $175.00 per share.
        assertEquals(40, holding.getShares());
        assertEquals(175.00, holding.getAverageCost(), TOLERANCE,
                "weighted average: 7000 / 40 = 175");

        // The wrong answer, which this test exists to rule out, is the plain
        // average of the two prices: (100 + 200) / 2 = 150
        assertTrue(Math.abs(holding.getAverageCost() - 150.00) > 1.0,
                "150 would be a plain average, ignoring how many shares were bought at each price");

        assertEquals(7000.00, holding.getCostBasis(), TOLERANCE);
    }

    @Test
    @DisplayName("Profit and return are measured against the cost basis")
    void profitAndReturn_useCostBasis() {
        Stock stock = stock();
        Holding holding = new Holding(stock);

        holding.addShares(20, 100.00);   // cost basis $2,000
        stock.setPrice(120.00);          // now worth $2,400

        assertEquals(2400.00, holding.getCurrentValue(), TOLERANCE);
        assertEquals(400.00, holding.getProfitLoss(), TOLERANCE);
        assertEquals(20.00, holding.getReturnPercent(), TOLERANCE, "400 / 2000 = 20%");
        assertTrue(holding.isProfitable());

        stock.setPrice(80.00);           // now worth $1,600
        assertEquals(-400.00, holding.getProfitLoss(), TOLERANCE);
        assertEquals(-20.00, holding.getReturnPercent(), TOLERANCE);
        assertFalse(holding.isProfitable());
    }


}
