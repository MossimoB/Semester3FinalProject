package org.mossimo.finalprojectsem3final.util;

import org.mossimo.finalprojectsem3final.model.Stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceGeneratorTest {

    private static final double TOLERANCE = 0.001;

    /** A normal stock: 6% volatility, slight upward trend */
    private Stock volatileStock() {
        return new Stock("NVT", "NovaTech", "Technology", "Test stock.",
                100.00, 0.06, 0.002);
    }

    /**
     * A stock with zero volatility. The random part of the formula multiplies by
     * zero, so its movement is entirely determined by its trend
     */
    private Stock predictableStock(double trend) {
        return new Stock("FLT", "FlatCo", "Testing", "Zero volatility.",
                100.00, 0.0, trend);
    }

    /*
            group 1 - reproducibility
     */
    @Test
    @DisplayName("the same seed produces exactly the same market")
    void sameSeed_producesSameSequence() {
        PriceGenerator first = new PriceGenerator(42L);
        PriceGenerator second = new PriceGenerator(42L);

        Stock stockA = volatileStock();
        Stock stockB = volatileStock();

        for (int i = 0; i < 50; i++) {
            first.tick(stockA);
            second.tick(stockB);
        }

        assertEquals(stockA.getCurrentPrice(), stockB.getCurrentPrice(), TOLERANCE,
                "two generators with seed 42 must produce identical prices");

        assertEquals(stockA.getPriceHistory(), stockB.getPriceHistory(),
                "the entire history must match, not just the final price");
    }

    @Test
    @DisplayName("Different seeds produce different markets")
    void differentSeeds_produceDifferentSequences() {
        PriceGenerator first = new PriceGenerator(1L);
        PriceGenerator second = new PriceGenerator(999L);

        Stock stockA = volatileStock();
        Stock stockB = volatileStock();

        for (int i = 0; i < 50; i++) {
            first.tick(stockA);
            second.tick(stockB);
        }

        // With 6% volatility over 50 ticks, two different seeds landing on the
        // same price to within a tenth of a cent is effectively impossible
        assertNotEquals(stockA.getCurrentPrice(), stockB.getCurrentPrice(),
                "different seeds should give a different market");
    }

    /*
            group 2 - the math without the randomness
     */
    @Test
    @DisplayName("A stock with no volatility and no trend never moves")
    void zeroVolatilityZeroTrend_priceNeverChanges() {
        PriceGenerator generator = new PriceGenerator(7L);
        Stock stock = predictableStock(0.0);

        for (int i = 0; i < 100; i++) {
            generator.tick(stock);
        }

        // percentChange = 0 + (0 × Z) = 0, so newPrice = price × 1 = price
        assertEquals(100.00, stock.getCurrentPrice(), TOLERANCE,
                "with no volatility and no trend the price is frozen");
    }

    @Test
    @DisplayName("Trend compounds: 1% per tick for 10 ticks is not 10%")
    void trendCompounds() {
        PriceGenerator generator = new PriceGenerator(7L);
        Stock stock = predictableStock(0.01);   // +1% per tick, exactly

        for (int i = 0; i < 10; i++) {
            generator.tick(stock);
        }

        // Because each tick multiplies, this is 100 × 1.01^10, not 100 + 10%
        //   100 × 1.01^10 = 110.4622...
        double expected = 100.00 * Math.pow(1.01, 10);

        assertEquals(expected, stock.getCurrentPrice(), 0.01,
                "growth compounds, so 10 ticks of +1% gives +10.46%, not +10.00%");

        assertEquals(110.46, stock.getCurrentPrice(), 0.01,
                "sanity check against the number worked out by hand");
    }

    @Test
    @DisplayName("tick returns the percentage it actually applied")
    void tick_returnsTheAppliedPercentage() {
        PriceGenerator generator = new PriceGenerator(7L);
        Stock stock = predictableStock(0.03);   // +3% per tick, exactly

        double returned = generator.tick(stock);

        assertEquals(3.00, returned, TOLERANCE,
                "the return value is a percentage, so +3% comes back as 3.0");

        // Agrees with what the stock itself reports
        assertEquals(stock.getPercentChange(), returned, TOLERANCE,
                "the returned percentage must match the stock's own calculation");
    }

    /*
            group 3 - properties that must hold for ANY random draw
     */
    @Test
    @DisplayName("A single tick can never move more than 25%")
    void tick_isClampedTo25Percent() {
        // Volatility of 5.0 is absurd (500%) which guarantees the raw formula
        // blows past the clamp on essentially every draw. That is the point: it
        // proves the clamp is doing the work
        Stock wild = new Stock("WLD", "WildCo", "Testing", "Absurd volatility.",
                100.00, 5.0, 0.0);

        PriceGenerator generator = new PriceGenerator(3L);

        for (int i = 0; i < 500; i++) {
            double appliedPercent = generator.tick(wild);

            assertTrue(Math.abs(appliedPercent) <= 25.0 + TOLERANCE,
                    "tick " + i + " applied " + appliedPercent + "%, which exceeds the clamp");
        }

        // Check the helper directly too.
        assertEquals(0.25, PriceGenerator.clamp(9.9), TOLERANCE);
        assertEquals(-0.25, PriceGenerator.clamp(-9.9), TOLERANCE);
        assertEquals(0.05, PriceGenerator.clamp(0.05), TOLERANCE,
                "a value already inside the range passes through untouched");
    }

    @Test
    @DisplayName("A price never falls below the $1.00 floor, however unlucky")
    void price_neverFallsBelowFloor() {
        // Strong volatility plus a strongly negative trend: this company is
        // being driven into the ground on purpose
        Stock doomed = new Stock("DED", "DoomedCo", "Testing", "Falling fast.",
                100.00, 0.20, -0.20);

        PriceGenerator generator = new PriceGenerator(11L);

        for (int i = 0; i < 2000; i++) {
            generator.tick(doomed);

            assertTrue(doomed.getCurrentPrice() >= Stock.MINIMUM_PRICE,
                    "price fell to " + doomed.getCurrentPrice() + " on tick " + i);
        }

        // After 2000 ticks of -20% drift it should be pinned at the floor
        assertEquals(Stock.MINIMUM_PRICE, doomed.getCurrentPrice(), TOLERANCE,
                "a company driven down for 2000 hours should rest on the floor");
    }

    /*
            group 4 - supporting methods that we will use as the project gets bigger
     */
    @Test
    @DisplayName("applyShock, randomBetween, pickRandom and rollChance behave")
    void supportingMethods_behave() {
        PriceGenerator generator = new PriceGenerator(5L);

        // --- applyShock applies an exact percentage ---
        Stock stock = volatileStock();
        generator.applyShock(stock, 8.5);

        assertEquals(108.50, stock.getCurrentPrice(), TOLERANCE,
                "a +8.5% shock on $100.00 gives $108.50");

        generator.applyShock(stock, -50.0);
        assertEquals(54.25, stock.getCurrentPrice(), TOLERANCE,
                "a -50% shock halves it");

        // --- randomBetween stays inside its band ---
        for (int i = 0; i < 200; i++) {
            double value = generator.randomBetween(5.0, 10.0);
            assertTrue(value >= 5.0 && value < 10.0,
                    "randomBetween(5,10) returned " + value);
        }

        // --- pickRandom returns a member of the list, and null for empty ---
        List<String> symbols = List.of("NVT", "GRG", "MDC");
        for (int i = 0; i < 50; i++) {
            assertTrue(symbols.contains(generator.pickRandom(symbols)),
                    "pickRandom must return an item from the list");
        }
        assertNull(generator.pickRandom(new ArrayList<String>()),
                "an empty list has nothing to pick");

        // --- rollChance(0) is never true, rollChance(1) is always true ---
        for (int i = 0; i < 100; i++) {
            assertTrue(!generator.rollChance(0.0), "probability 0 must never fire");
            assertTrue(generator.rollChance(1.0), "probability 1 must always fire");
        }
    }
}
