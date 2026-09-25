package org.mossimo.finalprojectsem3final.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsCalculatorTest {

    private static final double TOLERANCE = 0.001;

    /**
     *   values = 2, 4, 4, 4, 5, 5, 7, 9      (n = 8)
     *   sum    = 40
     *   mean   = 40 / 8 = 5
     */
    private List<Double> textbook() {
        return List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
    }

    /*
            center
     */
    @Test
    @DisplayName("mean adds everything and divides by the count")
    void mean_isTheArithmeticAverage() {
        // (2+4+4+4+5+5+7+9) / 8  =  40 / 8  =  5
        assertEquals(5.0, StatisticsCalculator.mean(textbook()), TOLERANCE);

        assertEquals(100.0, StatisticsCalculator.mean(List.of(100.0)), TOLERANCE);

        // An empty list returns 0 rather than throwing, because a stock with no
        // history is a normal state on the first frame, not an error
        assertEquals(0.0, StatisticsCalculator.mean(List.of()), TOLERANCE);
        assertEquals(0.0, StatisticsCalculator.mean(null), TOLERANCE);
    }

    @Test
    @DisplayName("median is the middle value, averaging the two centre values if even")
    void median_handlesOddAndEvenCounts() {
        // Odd count: 1, 3, 7  →  middle is 3
        assertEquals(3.0, StatisticsCalculator.median(List.of(1.0, 3.0, 7.0)), TOLERANCE);

        // Even count: 1, 3, 7, 9  →  (3 + 7) / 2 = 5
        assertEquals(5.0, StatisticsCalculator.median(List.of(1.0, 3.0, 7.0, 9.0)), TOLERANCE);

        // Unsorted input must still work
        assertEquals(5.0, StatisticsCalculator.median(List.of(9.0, 1.0, 7.0, 3.0)), TOLERANCE);

        // textbook sorted: 2,4,4,4,5,5,7,9  →  (4 + 5) / 2 = 4.5
        assertEquals(4.5, StatisticsCalculator.median(textbook()), TOLERANCE);
    }

    @Test
    @DisplayName("median does not reorder the caller's list")
    void median_doesNotMutateTheInput() {
        List<Double> priceHistory = new ArrayList<>(List.of(9.0, 1.0, 7.0, 3.0));

        StatisticsCalculator.median(priceHistory);

        // If median() sorted in place, a stock's price history would silently
        // become ascending and the chart would draw a straight rising line
        assertEquals(List.of(9.0, 1.0, 7.0, 3.0), priceHistory,
                "the caller's list must come back in its original order");
    }

    @Test
    @DisplayName("The mean and median disagree when the data is skewed")
    void mean_isDraggedByOutliers_medianIsNot() {
        // Nine hours near $150 then one spike to $600
        List<Double> spiky = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            spiky.add(150.0);
        }
        spiky.add(600.0);

        // mean = (9 × 150 + 600) / 10 = 1950 / 10 = 195
        assertEquals(195.0, StatisticsCalculator.mean(spiky), TOLERANCE);

        // The median ignores HOW FAR the outlier is, only that it is above the
        // middle, so it still reports the typical price
        assertEquals(150.0, StatisticsCalculator.median(spiky), TOLERANCE);

        assertTrue(StatisticsCalculator.mean(spiky) > StatisticsCalculator.median(spiky),
                "a single high outlier pulls the mean above the median");
    }

    /*
            spread
     */
    @Test
    @DisplayName("variance and standard deviation, worked through by hand")
    void variance_andStandardDeviation() {
        // values: 2, 4, 4, 4, 5, 5, 7, 9      mean = 5
        //
        // deviations:      -3, -1, -1, -1,  0,  0,  2,  4
        // squared:           9,  1,  1,  1,  0,  0,  4, 16   →  sum = 32
        //
        // variance = 32 / 8 = 4
        // stdDev   = √4     = 2
        assertEquals(4.0, StatisticsCalculator.variance(textbook()), TOLERANCE);
        assertEquals(2.0, StatisticsCalculator.standardDeviation(textbook()), TOLERANCE);

        // A list where every value is identical has no spread at all.
        assertEquals(0.0, StatisticsCalculator.standardDeviation(
                List.of(7.0, 7.0, 7.0, 7.0)), TOLERANCE);

        // Fewer than two values means spread is undefined; return 0, not NaN.
        assertEquals(0.0, StatisticsCalculator.variance(List.of(5.0)), TOLERANCE);
    }

    @Test
    @DisplayName("Coefficient of variation compares risk across different price levels")
    void coefficientOfVariation_comparesAcrossPrices() {
        // An expensive stock: mean 150, stdDev 15  →  10%
        List<Double> expensive = List.of(135.0, 150.0, 165.0, 150.0);
        // A cheap stock: mean 40, stdDev 4         →  10%
        List<Double> cheap = List.of(36.0, 40.0, 44.0, 40.0);

        // Their standard deviations are wildly different...
        assertTrue(StatisticsCalculator.standardDeviation(expensive)
                        > StatisticsCalculator.standardDeviation(cheap) * 3,
                "the expensive stock moves in much bigger dollar amounts");

        // ...but relative to their own price they are equally risky.
        assertEquals(StatisticsCalculator.coefficientOfVariation(expensive),
                StatisticsCalculator.coefficientOfVariation(cheap), 0.01,
                "the coefficient of variation makes them directly comparable");
    }

    /*
            extremes and change
     */
    @Test
    @DisplayName("min, max, range and percentile")
    void extremes() {
        List<Double> values = textbook();

        assertEquals(2.0, StatisticsCalculator.min(values), TOLERANCE);
        assertEquals(9.0, StatisticsCalculator.max(values), TOLERANCE);
        assertEquals(7.0, StatisticsCalculator.range(values), TOLERANCE, "9 - 2");

        // Sorted: 2,4,4,4,5,5,7,9. Index (int)(8 × 0.5) = 4 → value 5
        assertEquals(5.0, StatisticsCalculator.percentile(values, 0.50), TOLERANCE);
        assertEquals(2.0, StatisticsCalculator.percentile(values, 0.0), TOLERANCE);

        // Fraction 1.0 would index off the end; it must clamp, not throw
        assertEquals(9.0, StatisticsCalculator.percentile(values, 1.0), TOLERANCE);

        assertEquals(0.0, StatisticsCalculator.range(List.of()), TOLERANCE);
    }

    @Test
    @DisplayName("Moving average smooths the last N values")
    void movingAverage_smoothsRecentValues() {
        List<Double> prices = List.of(10.0, 20.0, 30.0, 40.0, 50.0);

        // Last 3: (30 + 40 + 50) / 3 = 40
        assertEquals(40.0, StatisticsCalculator.movingAverage(prices, 3), TOLERANCE);

        // A window larger than the data averages everything available, so the
        // line can start drawing from the very first tick
        assertEquals(30.0, StatisticsCalculator.movingAverage(prices, 99), TOLERANCE,
                "(10+20+30+40+50)/5 = 30");

        List<Double> series = StatisticsCalculator.movingAverageSeries(prices, 3);
        assertEquals(5, series.size(), "one point per input value, for drawing");
        assertEquals(10.0, series.get(0), TOLERANCE, "first point averages just itself");
        assertEquals(15.0, series.get(1), TOLERANCE, "(10+20)/2");
        assertEquals(20.0, series.get(2), TOLERANCE, "(10+20+30)/3");
        assertEquals(40.0, series.get(4), TOLERANCE, "(30+40+50)/3");
    }

    @Test
    @DisplayName("Max drawdown finds the worst peak-to-trough fall")
    void maxDrawdown_findsTheWorstFall() {
        // Rises to 200, crashes to 80, recovers to 150
        // Worst fall: from the 200 peak down to 80  →  (200-80)/200 = 60%
        List<Double> boomAndBust = List.of(100.0, 150.0, 200.0, 120.0, 80.0, 110.0, 150.0);

        assertEquals(60.0, StatisticsCalculator.maxDrawdownPercent(boomAndBust), TOLERANCE);

        // A price that only ever rises has no drawdown at all, however volatile it looks
        // This is why drawdown is a better measure of how frightening
        // an investment is than standard deviation
        List<Double> onlyUp = List.of(10.0, 40.0, 90.0, 200.0);
        assertEquals(0.0, StatisticsCalculator.maxDrawdownPercent(onlyUp), TOLERANCE);
        assertTrue(StatisticsCalculator.standardDeviation(onlyUp) > 0,
                "but it still has a large standard deviation");

        assertEquals(0.0, StatisticsCalculator.maxDrawdownPercent(List.of(5.0)), TOLERANCE);
    }
}
