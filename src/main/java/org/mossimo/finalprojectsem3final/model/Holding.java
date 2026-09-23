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
    /**
     * Adds shares and recalculates the weighted average cost
     *
     *   totalPaid  = (oldShares × oldAverage) + (newShares × price)
     *   newAverage = totalPaid / (oldShares + newShares)
     *
     * The first term is what you already had invested; the second is what you
     * just spent. Divide the combined cost by the combined share count and you
     * have the new average
     *
     * @param quantity how many shares to add. Must be positive.
     * @param pricePerShare what was paid per share in this purchase.
     */
    public void addShares(int quantity, double pricePerShare) {
        if (quantity <= 0) {
            return;
        }

        // Work out the total money invested BEFORE changing the share count,
        // because the old count is part of the calculation
        double totalPaid = (shares * averageCost) + (quantity * pricePerShare);

        shares += quantity;
        averageCost = totalPaid / shares;
    }

    /**
     * Removes shares after a sale
     *
     * @param quantity how many shares to remove.
     */
    public void removeShares(int quantity) {
        if (quantity <= 0) {
            return;
        }

        shares -= quantity;

        if (shares <= 0) {
            shares = 0;
            averageCost = 0;
        }
    }

    /*
            money calculations
     */
    /**
     * What these shares are worth at today's price
     *
     * currentValue = shares × currentPrice
     */
    public double getCurrentValue() {
        return shares * stock.getCurrentPrice();
    }

    /**
     * What the player paid for the shares they still hold
     *
     * costBasis = shares × averageCost
     */
    public double getCostBasis() {
        return shares * averageCost;
    }

    /** Unrealised profit or loss */
    public double getProfitLoss() {
        return getCurrentValue() - getCostBasis();
    }

    /**
     * Unrealised profit or loss as a percentage of what was invested
     *
     * returnPercent = profitLoss / costBasis × 100
     *
     * Returns 0 for an empty holding rather than dividing by zero
     */
    public double getReturnPercent() {
        double cost = getCostBasis();
        if (cost == 0) {
            return 0;
        }
        return getProfitLoss() / cost * 100.0;
    }

    /**
     * How much the price would have to move for this position to break even,
     * as a percentage of the current price
     */
    public double getBreakEvenMovePercent() {
        double current = stock.getCurrentPrice();
        if (current == 0) {
            return 0;
        }
        return (averageCost - current) / current * 100.0;
    }

    /** True if this position is currently in profit. Used to colour the row */
    public boolean isProfitable() {
        return getProfitLoss() > 0;
    }

    /** True if the player has sold everything */
    public boolean isEmpty() {
        return shares <= 0;
    }

    /*
            getters
     */
    public Stock getStock() {
        return stock;
    }

    public String getSymbol() {
        return stock.getSymbol();
    }

    public int getShares() {
        return shares;
    }

    public double getAverageCost() {
        return averageCost;
    }

    @Override
    public String toString() {
        return String.format("%-4s %4d shares @ avg $%8.2f  now $%8.2f  P/L %+10.2f (%+.2f%%)",
                getSymbol(), shares, averageCost, stock.getCurrentPrice(),
                getProfitLoss(), getReturnPercent());
    }
}
