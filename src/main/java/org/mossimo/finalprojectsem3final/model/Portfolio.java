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

    /** Sells shares, if the player owns that many */
    public TradeResult sell(Stock stock, int quantity, int day, int hour) {
        if (quantity <= 0) {
            return TradeResult.invalidQuantity(quantity);
        }

        Holding holding = holdings.get(stock.getSymbol());
        int owned = (holding == null) ? 0 : holding.getShares();

        if (owned < quantity) {
            return TradeResult.insufficientShares(quantity, owned, stock.getSymbol());
        }

        double proceeds = quantity * stock.getCurrentPrice();

        // Realised profit: what you sold for, minus what those shares cost you
        double realisedProfit = (stock.getCurrentPrice() - holding.getAverageCost()) * quantity;

        cash += proceeds;
        holding.removeShares(quantity);

        // Drop the position entirely once it is empty, so the portfolio table
        // does not show rows with 0 shares
        if (holding.isEmpty()) {
            holdings.remove(stock.getSymbol());
        }

        Transaction transaction = Transaction.sell(stock, quantity, day, hour, realisedProfit);
        transactions.add(transaction);

        return TradeResult.success(transaction, String.format(
                "Sold %d %s at $%,.2f for $%,.2f (%s$%,.2f).",
                quantity, stock.getSymbol(), stock.getCurrentPrice(), proceeds,
                realisedProfit < 0 ? "-" : "+", Math.abs(realisedProfit)));
    }

    /** The largest number of shares of this stock the player can afford */
    public int getMaxAffordable(Stock stock) {
        if (stock.getCurrentPrice() <= 0) {
            return 0;
        }
        return (int) (cash / stock.getCurrentPrice());
    }

    /*
            valuation
     */
    /** How many shares of one symbol the player owns. 0 if none */
    public int getSharesOwned(String symbol) {
        Holding holding = holdings.get(symbol);
        return holding == null ? 0 : holding.getShares();
    }

    /** Market value of every share owned, not counting cash */
    public double getHoldingsValue() {
        return holdings.values().stream().mapToDouble(Holding::getCurrentValue).sum();
    }

    /** Cash plus holdings. This is the number the game is scored on */
    public double getTotalValue() {
        return cash + getHoldingsValue();
    }

    /** What the player paid for the shares they currently hold */
    public double getTotalInvested() {
        return holdings.values().stream().mapToDouble(Holding::getCostBasis).sum();
    }

    /** Unrealised profit across all open positions */
    public double getUnrealisedProfit() {
        return holdings.values().stream().mapToDouble(Holding::getProfitLoss).sum();
    }

    /** Profit locked in by selling. Sums the realised profit of every sale */
    public double getRealisedProfit() {
        return transactions.stream().mapToDouble(Transaction::getRealisedProfit).sum();
    }

    /** Total value minus what the player started with */
    public double getProfitLoss() {
        return getTotalValue() - startingCash;
    }

    /**
     * Overall rate of return on the starting balance.
     *
     * returnPercent = (totalValue - startingCash) / startingCash × 100
     */
    public double getReturnPercent() {
        if (startingCash == 0) {
            return 0;
        }
        return getProfitLoss() / startingCash * 100.0;
    }

    /** What fraction of the account is in shares rather than cash, 0 to 100 */
    public double getInvestedPercent() {
        double total = getTotalValue();
        if (total == 0) {
            return 0;
        }
        return getHoldingsValue() / total * 100.0;
    }

    /*
            access
     */
    public Holding getHolding(String symbol) {
        return holdings.get(symbol);
    }

    /** All open positions in the order they were first opened */
    public Collection<Holding> getHoldings() {
        return List.copyOf(holdings.values());
    }

    /** Every trade ever made, oldest first */
    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    /** How many trades the player has made. Used by achievements */
    public int getTradeCount() {
        return transactions.size();
    }

    public double getCash() {
        return cash;
    }

    public double getStartingCash() {
        return startingCash;
    }

    /** Directly sets cash */
    public void restoreCash(double amount) {
        this.cash = amount;
    }

    /** Restores a holding when loading a save */
    public void restoreHolding(Stock stock, int shares, double averageCost) {
        Holding holding = new Holding(stock);
        holding.addShares(shares, averageCost);
        holdings.put(stock.getSymbol(), holding);
    }

    /** Restores a past trade when loading a save */
    public void restoreTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public String toString() {
        return String.format("Cash $%,.2f + holdings $%,.2f = $%,.2f (%+.2f%%)",
                cash, getHoldingsValue(), getTotalValue(), getReturnPercent());
    }
}
