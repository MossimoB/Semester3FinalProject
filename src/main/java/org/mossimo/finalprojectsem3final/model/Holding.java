package org.mossimo.finalprojectsem3final.model;

/**
 * One position: the shares of a single {@link Stock} the player currently owns,
 * plus the weighted average price they paid for them.
 *
 * <p>A Holding exists only while {@code shares > 0}. {@link Portfolio} removes
 * it from the map entirely once the last share is sold, so an empty Holding
 * never shows up in the portfolio table.</p>
 */
public class Holding {

    /**
     * The company. Final, because a Holding is always about one company; buying
     * a different company creates a different Holding
     *
     * Holding keeps a reference to the live Stock object rather than copying
     * its price. That is deliberate because when the market ticks and the Stock's price
     * changes, {#getCurrentValue()} is immediately correct with no
     * bookkeeping. If this stored a copied price, every holding would need
     * updating on every tick and one missed update would show stale money
     */
    private final Stock stock;

    /** How many shares are owned right now. Never negative. */
    private int shares;

    /** The weighted average price paid per share. See the header. */
    private double averageCost;

    public Holding(Stock stock) {
        this.stock = stock;
        this.shares = 0;
        this.averageCost = 0;
    }

    /*
            changing the position
     */
}
