package org.mossimo.finalprojectsem3final.model;

/**
 * One completed trade that is kept forever so the player can review their history
 *
 * Created only by {Portfolio#buy} and {Portfolio#sell}, and only
 * after the trade has actually succeeded. A rejected trade produces a
 * {TradeResult} with no Transaction inside it
 *
 *
 * DESIGN NOTE: WHY EVERY FIELD IS FINAL
 * A transaction is a historical fact. Once you bought 20 shares at $104.50 on
 * day 3, that happened, and nothing later should be able to edit it. Making
 * every field final means the compiler enforces that for you
 *
 * This is called an immutable object, and it is the right shape for anything
 * that records history: log entries, audit trails, receipts, undo stacks
 */
public class Transaction {

    /* Which direction the trade went */
    public enum Type {
        BUY, SELL
    }

    /*
            fields - all final (see design note in the header)
     */
    private final Type type;
    private final String symbol;
    private final String companyName;
    private final int shares;
    private final double pricePerShare;

    /** Simulated day this happened on, 1 to 10 */
    private final int simulationDay;

    /** Simulated hour this happened at, 9 to 16 */
    private final int simulationHour;

    /**
     * For a SELL: how much money was actually made or lost on those shares
     * For a BUY: always 0, because buying does not realise anything yet
     *
     *      realisedProfit = (sellPrice - averageCostPaid) × shares
     *
     * This is the difference between realised and unrealised profit
     *
     *   Unrealised is what {Holding#getProfitLoss()} reports:
     *          paper profit on shares you still own. It changes every tick
     *   Realised is this field:
     *          profit you actually locked in by selling (it never changes again)
     *
     * Achievements use this to award "sold at a profit five times
     * in a row", which is impossible to check from unrealised numbers
     */
    private final double realisedProfit;

    /**
     * Records a completed trade
     *
     * Prefer the two factory methods {#buy} and {#sell} below
     * They read better at the call site and make it impossible to pass a
     * realised profit on a purchase by mistake
     *
     * @param type           BUY or SELL
     * @param symbol         ticker symbol
     * @param companyName    full company name
     * @param shares         shares traded (always positive)
     * @param pricePerShare  price per share in dollars
     * @param simulationDay  day it happened on
     * @param simulationHour hour it happened at
     * @param realisedProfit profit locked in; 0 on a BUY and negative on a loss
     */

    public Transaction(Type type,
                       String symbol,
                       String companyName,
                       int shares,
                       double pricePerShare,
                       int simulationDay,
                       int simulationHour,
                       double realisedProfit) {

        this.type = type;
        this.symbol = symbol;
        this.companyName = companyName;
        this.shares = shares;
        this.pricePerShare = pricePerShare;
        this.simulationDay = simulationDay;
        this.simulationHour = simulationHour;
        this.realisedProfit = realisedProfit;
    }

    /*
            factory methods
     */
    /** Records a purchase. Realised profit is always 0 on a buy */
    public static Transaction buy(Stock stock, int shares, int day, int hour) {
        return new Transaction(Type.BUY, stock.getSymbol(), stock.getCompanyName(),
                shares, stock.getCurrentPrice(), day, hour, 0.0);
    }

    /** Records a sale, along with the profit or loss it locked in */
    public static Transaction sell(Stock stock, int shares, int day, int hour,
                                   double realisedProfit) {
        return new Transaction(Type.SELL, stock.getSymbol(), stock.getCompanyName(),
                shares, stock.getCurrentPrice(), day, hour, realisedProfit);
    }

    /*
            calculations
     */
    /**
     * The total money that changed hands
     *
     * total = shares × pricePerShare
     *
     * On a BUY this left the player's cash
     * On a SELL it came back in
     */
    public double getTotal() {
        return shares * pricePerShare;
    }

    /** True if this was a purchase. Used by (future) history table */
    public boolean isBuy() {
        return type == Type.BUY;
    }

    /** True if this sale made money. Always false for a purchase */
    public boolean isProfitable() {
        return type == Type.SELL && realisedProfit > 0;
    }

    /**
     * A sortable timestamp. Day 3 at 14:00 becomes 314
     *
     * orderKey = day × 100 + hour
     */
    public int getOrderKey() {
        return simulationDay * 100 + simulationHour;
    }

    /*
            getters - no setters because this object never changes
     */
    public Type getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getShares() {
        return shares;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    public int getSimulationDay() {
        return simulationDay;
    }

    public int getSimulationHour() {
        return simulationHour;
    }

    public double getRealisedProfit() {
        return realisedProfit;
    }

    /*
            object methods
     */
    @Override
    public String toString() {
        String base = String.format("Day %2d %02d:00  %-4s %3d x %-4s @ $%8.2f = $%10.2f",
                simulationDay, simulationHour, type, shares, symbol, pricePerShare, getTotal());

        if (type == Type.SELL) {
            base += String.format("   realised %+.2f", realisedProfit);
        }
        return base;
    }
}
