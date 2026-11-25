package org.mossimo.finalprojectsem3final.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClockSpeedTest {

    private static final double TOLERANCE = 0.0001;

    @Test
    @DisplayName("next() steps through every speed and wraps back to the start")
    void next_cyclesAndWraps() {
        assertEquals(ClockSpeed.FAST, ClockSpeed.NORMAL.next());
        assertEquals(ClockSpeed.FASTER, ClockSpeed.FAST.next());
        assertEquals(ClockSpeed.TURBO, ClockSpeed.FASTER.next());

        assertEquals(ClockSpeed.NORMAL, ClockSpeed.TURBO.next(),
                "the cycle must wrap, not run off the end of the array");
    }

    @Test
    @DisplayName("Cycling through every speed returns to where it started")
    void next_returnsToStartAfterAFullCycle() {
        ClockSpeed speed = ClockSpeed.NORMAL;

        for (int i = 0; i < ClockSpeed.values().length; i++) {
            speed = speed.next();
        }

        assertEquals(ClockSpeed.NORMAL, speed,
                "pressing Speed once per available speed should return to 1x");
    }

    @Test
    @DisplayName("Multipliers increase and seconds-per-tick decrease")
    void multipliers_areConsistent() {
        assertEquals(1.0, ClockSpeed.NORMAL.getMultiplier(), TOLERANCE);
        assertEquals(10.0, ClockSpeed.TURBO.getMultiplier(), TOLERANCE);

        assertEquals(1.0, ClockSpeed.NORMAL.getSecondsPerTick(), TOLERANCE);
        assertEquals(0.25, ClockSpeed.FASTER.getSecondsPerTick(), TOLERANCE,
                "at 4x one simulated hour takes a quarter of a second");

        ClockSpeed[] all = ClockSpeed.values();
        for (int i = 1; i < all.length; i++) {
            assertTrue(all[i].getMultiplier() > all[i - 1].getMultiplier(),
                    all[i] + " should be faster than " + all[i - 1]);
        }
    }

    @Test
    @DisplayName("fromMultiplier finds a match and falls back safely")
    void fromMultiplier_findsOrFallsBack() {
        assertEquals(ClockSpeed.FAST, ClockSpeed.fromMultiplier(2.0));
        assertEquals(ClockSpeed.TURBO, ClockSpeed.fromMultiplier(10.0));

        assertEquals(ClockSpeed.NORMAL, ClockSpeed.fromMultiplier(3.7),
                "an unrecognised speed should fall back to 1x, not throw");
        assertEquals(ClockSpeed.NORMAL, ClockSpeed.fromMultiplier(-5.0));

        for (ClockSpeed speed : ClockSpeed.values()) {
            assertNotNull(speed.getLabel());
            assertTrue(speed.getLabel().endsWith("x"), "labels read like '2x'");
        }
    }
}
