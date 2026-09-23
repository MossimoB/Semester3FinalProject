package org.mossimo.finalprojectsem3final.model;

public class TradeResult {

    /** Why a trade was rejected */
    public enum Failure {
        /** The trade went through */
        NONE,
        /** Quantity was zero or negative */
        INVALID_QUANTITY,
        /** Not enough cash to buy that many shares */
        INSUFFICIENT_FUNDS,
        /** The player does not own that many shares */
        INSUFFICIENT_SHARES,
        /** The simulation has finished; the market is closed */
        MARKET_CLOSED
    }

    private final boolean successful;
    private final Failure failure;
    private final String message;
    /** The completed trade or null if the trade was rejected */
    private final Transaction transaction;

    private TradeResult(boolean successful, Failure failure, String message,
                        Transaction transaction) {
        this.successful = successful;
        this.failure = failure;
        this.message = message;
        this.transaction = transaction;
    }

    /** The trade went through */
    public static TradeResult success(Transaction transaction, String message) {
        return new TradeResult(true, Failure.NONE, message, transaction);
    }

    /** The trade was rejected for the given reason */
    public static TradeResult rejected(Failure failure, String message) {
        return new TradeResult(false, failure, message, null);
    }

    /*
            convenience builders for the four rejection cases - messages player gets
     */

    public static TradeResult invalidQuantity(int requested) {
        return rejected(Failure.INVALID_QUANTITY,
                String.format("Enter a quantity of at least 1. You asked for %d.", requested));
    }

    public static TradeResult insufficientFunds(double cost, double cashAvailable) {
        return rejected(Failure.INSUFFICIENT_FUNDS,
                String.format("Not enough cash. That costs $%,.2f and you have $%,.2f.",
                        cost, cashAvailable));
    }

    public static TradeResult insufficientShares(int requested, int owned, String symbol) {
        return rejected(Failure.INSUFFICIENT_SHARES,
                String.format("You tried to sell %d shares of %s but you only own %d.",
                        requested, symbol, owned));
    }

    public static TradeResult marketClosed() {
        return rejected(Failure.MARKET_CLOSED,
                "The simulation has finished. The market is closed.");
    }

    /*
            getters
     */
    /** True if the trade actually happened */
    public boolean isSuccessful() {
        return successful;
    }

    /** True if the trade was rejected */
    public boolean isRejected() {
        return !successful;
    }

    /**
     * Why it was rejected or {Failure#NONE} if it succeeded
     *
     * (Assert on this in tests)
     */
    public Failure getFailure() {
        return failure;
    }

    /** A complete sentence, safe to show the player as-is. */
    public String getMessage() {
        return message;
    }

    /**
     * The completed trade
     *
     * @return the {Transaction} or null if the trade was rejected
     *         Always check {#isSuccessful()} first
     */
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public String toString() {
        return (successful ? "OK: " : "REJECTED (" + failure + "): ") + message;
    }
}
