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


}
