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

}
