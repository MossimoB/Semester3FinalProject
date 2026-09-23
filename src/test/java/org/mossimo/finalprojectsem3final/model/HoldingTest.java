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
    void newHolding_isEmpty() {
        Holding holding = new Holding(stock());

        assertEquals(0, holding.getShares());
        assertEquals(0.0, holding.getAverageCost(), TOLERANCE);
        assertEquals(0.0, holding.getCurrentValue(), TOLERANCE);
        assertEquals(0.0, holding.getProfitLoss(), TOLERANCE);

        // Must not divide by zero on an empty holding.
        assertEquals(0.0, holding.getReturnPercent(), TOLERANCE);
        assertTrue(holding.isEmpty());
    }

    @Test
    @DisplayName("One purchase sets the average cost to the price paid")
    void singlePurchase_averageIsThePricePaid() {
        Holding holding = new Holding(stock());

        holding.addShares(20, 100.00);

        assertEquals(20, holding.getShares());
        assertEquals(100.00, holding.getAverageCost(), TOLERANCE);
        assertEquals(2000.00, holding.getCostBasis(), TOLERANCE);
        assertFalse(holding.isEmpty());
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

    @Test
    @DisplayName("Selling does not change the average cost of what is left")
    void removeShares_leavesAverageCostAlone() {
        Holding holding = new Holding(stock());

        holding.addShares(10, 100.00);
        holding.addShares(30, 200.00);   // average $175

        holding.removeShares(10);

        assertEquals(30, holding.getShares());
        assertEquals(175.00, holding.getAverageCost(), TOLERANCE,
                "the 30 shares still standing still cost $175 each");
        assertEquals(5250.00, holding.getCostBasis(), TOLERANCE, "30 x 175");
    }

    @Test
    @DisplayName("Selling everything resets the holding to empty")
    void removeAllShares_resetsToEmpty() {
        Holding holding = new Holding(stock());

        holding.addShares(25, 80.00);
        holding.removeShares(25);

        assertEquals(0, holding.getShares());
        assertEquals(0.0, holding.getAverageCost(), TOLERANCE,
                "a re-opened position must not inherit the old average");
        assertTrue(holding.isEmpty());

        // Over-selling clamps to zero rather than going negative
        holding.addShares(5, 50.00);
        holding.removeShares(999);
        assertEquals(0, holding.getShares(), "shares must never go negative");
    }

    @Test
    @DisplayName("Break-even move shows why big losses hurt so much")
    void breakEvenMove_isBiggerThanTheLoss() {
        Stock stock = stock();
        Holding holding = new Holding(stock);

        holding.addShares(10, 100.00);
        stock.setPrice(80.00);           // down 20%

        assertEquals(-20.00, holding.getReturnPercent(), TOLERANCE, "the position is down 20%");

        // But it needs a 25% RISE from $80 to get back to $100
        assertEquals(25.00, holding.getBreakEvenMovePercent(), TOLERANCE,
                "a 20% loss needs a 25% gain to undo: this asymmetry is volatility drag");
    }
}
