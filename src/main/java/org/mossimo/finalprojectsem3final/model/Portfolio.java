package org.mossimo.finalprojectsem3final.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The player's account: cash, open positions, and every trade they have made */
public class Portfolio {

    private final double startingCash;
    private double cash;

    /** Open positions keyed by ticker symbol */
    private final Map<String, Holding> holdings = new LinkedHashMap<>();

    /** Every completed trade, oldest first */
    private final List<Transaction> transactions = new ArrayList<>();

    public Portfolio(double startingCash) {
        this.startingCash = startingCash;
        this.cash = startingCash;
    }

    /*
            trading
     */

    /**
     * Buys shares only if the player can afford them
     *
     * Checks happen in a deliberate order: quantity first, then cost
     *
     * @return a {TradeResult} that is either successful or carries the
     *         reason it was rejected. Never null
     */
    public TradeResult buy(Stock stock, int quantity, int day, int hour) {
        if (quantity <= 0) {
            return TradeResult.invalidQuantity(quantity);
        }

        double cost = quantity * stock.getCurrentPrice();
        if (cost > cash) {
            return TradeResult.insufficientFunds(cost, cash);
        }

        cash -= cost;

        // computeIfAbsent creates the Holding on the first purchase of a company
        // and reuses it on every later purchase, which is what keeps the
        // weighted average cost correct across repeat buys
        holdings.computeIfAbsent(stock.getSymbol(), key -> new Holding(stock))
                .addShares(quantity, stock.getCurrentPrice());

        Transaction transaction = Transaction.buy(stock, quantity, day, hour);
        transactions.add(transaction);

        return TradeResult.success(transaction, String.format(
                "Bought %d %s at $%,.2f for $%,.2f.",
                quantity, stock.getSymbol(), stock.getCurrentPrice(), cost));
    }


}
