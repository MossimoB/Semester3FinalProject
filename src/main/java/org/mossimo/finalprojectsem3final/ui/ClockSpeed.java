package org.mossimo.finalprojectsem3final.ui;

public enum ClockSpeed {

    NORMAL(1.0, "1x"),
    FAST(2.0, "2x"),
    FASTER(4.0, "4x"),
    TURBO(10.0, "10x");

    private final double multiplier;
    private final String label;

    ClockSpeed(double multiplier, String label) {
        this.multiplier = multiplier;
        this.label = label;
    }

    public ClockSpeed next() {
        ClockSpeed[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public double getSecondsPerTick() {
        return 1.0 / multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getLabel() {
        return label;
    }

    public static ClockSpeed fromMultiplier(double multiplier) {
        for (ClockSpeed speed : values()) {
            if (Math.abs(speed.multiplier - multiplier) < 0.001) {
                return speed;
            }
        }
        return NORMAL;
    }

    @Override
    public String toString() {
        return label;
    }
}
