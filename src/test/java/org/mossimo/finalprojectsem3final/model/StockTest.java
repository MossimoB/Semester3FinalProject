package org.mossimo.finalprojectsem3final.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {Stock}.
 *
 * Naming convention used throughout this project: {methodUnderTest_whatShouldHappen}
 * When a test fails, Maven prints the method name,
 * so a good name tells us what broke without opening the file
 */
class StockTest {
    /**
     * Comparing doubles with {==} is unreliable, because 0.1 + 0.2 is not
     * exactly 0.3 in binary floating point
     *
     * Every assertion on a double needs a tolerance
     * One cent is far tighter than anything the player can see
     */
    private static final double TOLERANCE = 0.001;

    /**
     * A fresh test stock that is called at the start of each test rather than shared in
     * a field, so no test can leave state behind that breaks the next one
     *
     * NovaTech at $100 with volatility 0.06 is the same configuration the
     * real market uses, which makes these numbers easy to
     * reason about: 1% of $100 is $1
     */
    private Stock newTestStock() {
        return new Stock("NVT", "NovaTech", "Technology",
                "Consumer electronics and cloud services.",
                100.00, 0.06, 0.002);
    }

    /*
            group 1: the state of a brand-new stock
     */

    @Test
    @DisplayName("A new stock sits at its starting price")
    void newStock_startsAtStartingPrice() {
        Stock stock = newTestStock();

        assertEquals(100.00, stock.getCurrentPrice(), TOLERANCE,
                "currentPrice should equal startingPrice before any tick");

        // previousPrice must also be the starting price, not 0
        // If it were 0, getPercentChange() would divide by zero on the very first call
        assertEquals(100.00, stock.getPreviousPrice(), TOLERANCE,
                "previousPrice should also equal startingPrice before any tick");

        // A stock that has not moved has not changed
        assertEquals(0.0, stock.getPercentChange(), TOLERANCE,
                "a stock that has not ticked has not changed");
    }

    @Test
    @DisplayName("A new stock has exactly one price in its history")
    void newStock_hasOneHistoryEntry() {
        Stock stock = newTestStock();

        assertEquals(1, stock.getHistorySize(),
                "history should start with the opening price, not empty");

        assertEquals(100.00, stock.getPriceHistory().get(0), TOLERANCE,
                "the first history entry should be the starting price");
    }

    /*
            group 2: what setPrice does
     */

    @Test
    @DisplayName("setPrice moves the old current price into previous")
    void setPrice_movesCurrentPriceToPrevious() {
        Stock stock = newTestStock();

        stock.setPrice(110.00);

        assertEquals(110.00, stock.getCurrentPrice(), TOLERANCE,
                "currentPrice should be the new price");
        assertEquals(100.00, stock.getPreviousPrice(), TOLERANCE,
                "previousPrice should be the price from before the call");

        // Second tick: previous should now be the first new price, not the
        // original starting price. This catches the classic bug where previous
        // is assigned from startingPrice instead of currentPrice
        stock.setPrice(120.00);

        assertEquals(120.00, stock.getCurrentPrice(), TOLERANCE);
        assertEquals(110.00, stock.getPreviousPrice(), TOLERANCE,
                "previous should follow one step behind, every tick");
    }

    // Honest note, I had to Google how to do this part
    // It is my first time using history.get(n) but it is very useful
    //
    //
    // Note to self about what history.get(n) does:
    // history is a numbered row of prices. get(n) hands back the one in slot n, counting from 0
    //
    // history.get(0) → the stock's starting price
    // history.get(history.size() - 1) → the current price
    // history.get(n) → the price after the nth tick
    @Test
    @DisplayName("setPrice appends to the history and keeps the order")
    void setPrice_appendsToHistory() {
        Stock stock = newTestStock();

        stock.setPrice(105.00);
        stock.setPrice(98.00);
        stock.setPrice(103.00);

        assertEquals(4, stock.getHistorySize(),
                "1 opening price + 3 ticks = 4 entries");

        List<Double> history = stock.getPriceHistory();

        // Oldest first because the chart that we will add in the future
        // plots this list left to right and would end up
        // drawing the graph backwards if the order were ever reversed
        assertEquals(100.00, history.get(0), TOLERANCE, "index 0 = opening price");
        assertEquals(105.00, history.get(1), TOLERANCE);
        assertEquals(98.00,  history.get(2), TOLERANCE);
        assertEquals(103.00, history.get(3), TOLERANCE, "last entry = current price");

        assertEquals(stock.getCurrentPrice(), history.get(history.size() - 1), TOLERANCE,
                "the last history entry must always equal the current price");
    }

    /*
            group 3: percentage calculations
     */

    @Test
    @DisplayName("Percent change is positive when the price rises")
    void getPercentChange_positiveWhenPriceRises() {
        Stock stock = newTestStock();

        stock.setPrice(103.00);

        // (103 - 100) / 100 × 100 = +3.00%
        assertEquals(3.00, stock.getPercentChange(), TOLERANCE);

        // And the dollar change agrees with the percentage
        assertEquals(3.00, stock.getPriceChange(), TOLERANCE);
    }

    @Test
    @DisplayName("Percent change is negative when the price falls")
    void getPercentChange_negativeWhenPriceFalls() {
        Stock stock = newTestStock();

        stock.setPrice(92.50);

        // (92.50 - 100) / 100 × 100 = -7.50%
        assertEquals(-7.50, stock.getPercentChange(), TOLERANCE);
        assertEquals(-7.50, stock.getPriceChange(), TOLERANCE);

        // A second fall is measured against 92.50, NOT against the original 100
        // Getting this wrong makes a recovering stock look like it is still crashing
        stock.setPrice(83.25);

        // (83.25 - 92.50) / 92.50 × 100 = -10.00%
        assertEquals(-10.00, stock.getPercentChange(), TOLERANCE,
                "each tick is measured against the tick before it");
    }

    @Test
    @DisplayName("Total percent change is measured from day 1, not the last tick")
    void getTotalPercentChange_measuresFromStartingPrice() {
        Stock stock = newTestStock();

        stock.setPrice(120.00);
        stock.setPrice(110.00);

        // Since the last tick: (110 - 120) / 120 × 100 = -8.33%
        assertEquals(-8.333, stock.getPercentChange(), 0.01,
                "down since the previous hour");

        // Since day 1: (110 - 100) / 100 × 100 = +10.00%
        assertEquals(10.00, stock.getTotalPercentChange(), TOLERANCE,
                "but still up overall");

        // These two numbers having opposite signs is the whole point of showing
        // both on the stock detail screen (in the future)
    }

    /*
            group 4: safety rules
     */

    @Test
    @DisplayName("A price can never fall below the $1.00 floor")
    void setPrice_floorsAtMinimumPrice() {
        Stock stock = newTestStock();

        stock.setPrice(0.50);
        assertEquals(Stock.MINIMUM_PRICE, stock.getCurrentPrice(), TOLERANCE,
                "$0.50 should be raised to the $1.00 floor");

        stock.setPrice(-40.00);
        assertEquals(Stock.MINIMUM_PRICE, stock.getCurrentPrice(), TOLERANCE,
                "a negative price should be raised to the floor, not stored");

        stock.setPrice(0.0);
        assertEquals(Stock.MINIMUM_PRICE, stock.getCurrentPrice(), TOLERANCE,
                "zero should be raised to the floor");

        // The floored value is what goes into the history too, so the chart can
        // never plot a negative point
        List<Double> history = stock.getPriceHistory();
        for (double price : history) {
            assertTrue(price >= Stock.MINIMUM_PRICE,
                    "no history entry may be below the floor, found " + price);
        }
    }

    @Test
    @DisplayName("The price history cannot be modified from outside the class")
    void getPriceHistory_cannotBeModifiedFromOutside() {
        Stock stock = newTestStock();
        stock.setPrice(105.00);

        List<Double> history = stock.getPriceHistory();

        // getPriceHistory() returns an unmodifiable copy
        assertThrows(UnsupportedOperationException.class,
                () -> history.add(999.00),
                "the returned history must be unmodifiable");

        // The real history is untouched
        assertEquals(2, stock.getHistorySize(),
                "the stock's own history should still have 2 entries");
    }
}
