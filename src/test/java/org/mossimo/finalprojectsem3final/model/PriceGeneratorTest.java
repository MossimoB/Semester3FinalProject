package org.mossimo.finalprojectsem3final.model;

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
}
