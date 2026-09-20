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
}
