package org.mossimo.finalprojectsem3final.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StatisticsCalculator {

    private StatisticsCalculator() {
    }

    /*
            center
     */
    /**
     * The arithmetic mean: add everything, divide by how many
     *
     * mean = Σx / n
     */
    public static double mean(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /**
     * The median: the middle value once sorted
     *
     * With an even count there is no single middle, so the two central values
     * are averaged
     */
    public static double median(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        // Copy before sorting
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        int middle = sorted.size() / 2;

        if (sorted.size() % 2 == 1) {
            return sorted.get(middle);
        }
        return (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
    }

    /*
            spread
     */
    /**
     * The population variance: the mean of the squared distances from the mean
     *
     * variance = Σ(x - mean)² / n
     *
     * Distances are squared for two reasons: it makes them all positive, so
     * values above and below the mean do not cancel out, and it weights a large
     * deviation much more heavily than a small one
     */
    public static double variance(List<Double> values) {
        if (values == null || values.size() < 2) {
            return 0;
        }

        double mean = mean(values);

        double sumOfSquaredDeviations = values.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .sum();

        return sumOfSquaredDeviations / values.size();
    }

    /**
     * The standard deviation: the square root of the variance
     *
     * stdDev = √variance
     *
     * The square root undoes the squaring, so the answer comes back in the original units
     *
     * This is the number shown as "Volatility" on the statistics panel
     */
    public static double standardDeviation(List<Double> values) {
        return Math.sqrt(variance(values));
    }

    /**
     * The coefficient of variation: standard deviation as a percentage of the mean
     *
     * cv = stdDev / mean × 100
     *
     * This is what lets you compare risk between companies at different
     * prices. QuantumAI at $150 swinging by $15 and FreshBite at $40 swinging by
     * $4 have very different standard deviations but almost the same coefficient
     * of variation, and it is the second number that tells you they are similarly
     * risky relative to their price
     */
    public static double coefficientOfVariation(List<Double> values) {
        double mean = mean(values);
        if (mean == 0) {
            return 0;
        }
        return standardDeviation(values) / mean * 100.0;
    }

    /*
            extremes
     */
    public static double min(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        return values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    public static double max(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        return values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    /** The distance between the highest and lowest values */
    public static double range(List<Double> values) {
        return max(values) - min(values);
    }

    /**
     * The value below which the given fraction of the data falls
     *
     * Uses the nearest-rank method rather than interpolating
     *
     * @param fraction 0.5 for the median, 0.9 for the 90th percentile
     */
    public static double percentile(List<Double> values, double fraction) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        int index = (int) (sorted.size() * fraction);
        index = Math.max(0, Math.min(index, sorted.size() - 1));
        return sorted.get(index);
    }

    /*
            change over time
     */
    /**
     * Percentage change between two values
     *
     * percentChange = (newValue - oldValue) / oldValue × 100
     */
    public static double percentChange(double oldValue, double newValue) {
        if (oldValue == 0) {
            return 0;
        }
        return (newValue - oldValue) / oldValue * 100.0;
    }

    /** The simple moving average over the last {window} values */
    public static double movingAverage(List<Double> values, int window) {
        if (values == null || values.isEmpty() || window <= 0) {
            return 0;
        }
        int from = Math.max(0, values.size() - window);
        return mean(values.subList(from, values.size()));
    }

    /**
     * A moving average at every point, for drawing as a line
     *
     * @return a list the same length as the input
     */
    public static List<Double> movingAverageSeries(List<Double> values, int window) {
        List<Double> series = new ArrayList<>();
        if (values == null) {
            return series;
        }
        for (int i = 0; i < values.size(); i++) {
            int from = Math.max(0, i - window + 1);
            series.add(mean(values.subList(from, i + 1)));
        }
        return series;
    }

    /**
     * The largest peak-to-trough fall in the data, as a positive percentage
     *
     * Known as maximum drawdown
     * It answers "what is the worst loss someone could have suffered by buying at the wrong moment and selling at
     * the worst moment afterwards?"
     *
     * This is a better measure of how frightening an investment is than
     * standard deviation, because it only counts downside
     * A stock that only ever rises has a high standard deviation and zero drawdown
     *
     * The algorithm walks forward once, tracking the highest point seen so
     * far and the biggest fall from it. One pass, no nested loop
     */
    public static double maxDrawdownPercent(List<Double> values) {
        if (values == null || values.size() < 2) {
            return 0;
        }

        double peak = values.get(0);
        double worstFall = 0;

        for (double value : values) {
            if (value > peak) {
                peak = value;
            }
            if (peak > 0) {
                double fall = (peak - value) / peak * 100.0;
                if (fall > worstFall) {
                    worstFall = fall;
                }
            }
        }
        return worstFall;
    }
}
