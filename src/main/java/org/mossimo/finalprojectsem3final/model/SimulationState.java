package org.mossimo.finalprojectsem3final.model;

public enum SimulationState {

    /**
     * Built but never played. The clock has not moved.
     */
    NOT_STARTED("Ready"),

    /**
     * Time is passing and prices are moving.
     */
    RUNNING("Running"),

    /**
     * Time is stopped. The player can still trade at the frozen prices.
     */
    PAUSED("Paused"),

    /**
     * The last day is over. No more ticks, no more trading.
     */
    FINISHED("Finished");

    private final String displayName;

    SimulationState(String displayName) {
        this.displayName = displayName;
    }

    /**
     * True if pressing Play should do something.
     */
    public boolean canStart() {
        return this == NOT_STARTED || this == PAUSED;
    }

    /**
     * True if pressing Pause should do something.
     */
    public boolean canPause() {
        return this == RUNNING;
    }
}
