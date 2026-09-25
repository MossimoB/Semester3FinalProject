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

}
