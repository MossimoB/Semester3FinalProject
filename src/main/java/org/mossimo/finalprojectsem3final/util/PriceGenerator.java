package org.mossimo.finalprojectsem3final.util;

import org.mossimo.finalprojectsem3final.model.Stock;

import java.util.Random;

/**
 * Generates simulated market movement, one step per simulated hour:
 *
 *   percentChange = trend + (volatility × Z)     where Z ~ N(0, 1)
 *   newPrice      = currentPrice × (1 + percentChange)
 *
 * Why it's built this way:
 *
 * - Percent, not dollars: a $150 stock moves in bigger dollar steps than a
 *   $40 one at the same volatility, like real markets
 *
 * - Gaussian Z, not uniform: {nextGaussian()} makes small moves common
 *   and large moves rare, so the chart looks like a price chart.
 *   {nextDouble()} would make +6% as likely as +0.1%
 *
 * - Trend is added: it shifts the bell curve off zero, so a stock drifts
 *   upward over time while any single hour can still be down
 *
 * Example: price $100.00, volatility 0.06, trend 0.002, Z = +0.8
 *          → 0.002 + 0.048 = +5.0% → $105.00
 */

public class PriceGenerator {

    /**
     * The largest move allowed in a single tick, as a fraction. 0.25 = 25%
     */
    public static final double MAX_CHANGE_PER_TICK = 0.25;

    /**
     * The source of randomness
     */
    private final Random random;

    public PriceGenerator() {
        this.random = new Random();
    }

    /**
     * Seeded constructor, used by tests and by the future save files
     *
     * The same seed always produces the same sequence of prices. That is what
     * makes {PriceGeneratorTest} possible: you cannot write an assertion
     * about a number you cannot predict
     *
     * Loading a game resumes the same market rather than a new one
     *
     * @param seed any long. The same seed gives the same market, every time
     */
    public PriceGenerator(long seed) {
        this.random = new Random(seed);
    }

    /*
            the main method
     */

    /**
     * Calculates and applies one price movement for the given stock
     *
     * Called once per stock per simulated hour
     *
     * @param stock the stock to move. It is modified in place
     * @return the percentage change that was actually applied
     */
    public double tick(Stock stock) {

        // STEP 1. Draw a standard normal random value
        double z = random.nextGaussian();

        // STEP 2. Scale it by this stock's volatility and shift it by its trend
        double fractionalChange = stock.getTrend() + (stock.getVolatility() * z);

        // STEP 3. Clamp, so one freak draw cannot destroy a company
        fractionalChange = clamp(fractionalChange);

        // STEP 4. Apply it. Stock.setPrice handles the $1.00 floor and the
        //         history bookkeeping
        double newPrice = stock.getCurrentPrice() * (1 + fractionalChange);
        stock.setPrice(newPrice);

        // STEP 5. Hand back the change as a percentage for display
        return fractionalChange * 100.0;
    }

    /*
            supporting methods
     */

}
