package org.mossimo.finalprojectsem3final.demo;

import org.mossimo.finalprojectsem3final.model.Holding;
import org.mossimo.finalprojectsem3final.model.Market;
import org.mossimo.finalprojectsem3final.model.Portfolio;
import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.model.Transaction;
import org.mossimo.finalprojectsem3final.util.PriceGenerator;

/**
 * Runs a ten-day game with a robot player, and prints what it did.
 *
 * The strategy is deliberately naive: buy the cheapest thing that looks like
 * it is recovering, sell anything up 15% or down 10%. It is not meant to be
 * good. It is meant to BUY and SELL hundreds of times so we can see the numbers move
 */
public class TradingDemo {

    private static final int HOURS_PER_DAY = 8;
    private static final int DAYS = 10;
    private static final long SEED = 84L;
    private static final double STARTING_CASH = 10_000.00;
    private static final double TARGET = 15_000.00;

    /** Take profit at +15%, cut losses at -10% */
    private static final double TAKE_PROFIT_PERCENT = 15.0;
    private static final double STOP_LOSS_PERCENT = -10.0;

    public static void main(String[] args) {

        PriceGenerator generator = new PriceGenerator(SEED);
        Market market = Market.createDefaultMarket(generator);
        Portfolio portfolio = new Portfolio(STARTING_CASH);

        System.out.println();
        System.out.println("  STOCKSIM  ·  TRADING DEMO");
        System.out.println("  A robot plays ten days with a simple rule-based strategy.");
        System.out.printf("  Start $%,.2f    Target $%,.2f    Seed %d%n%n", STARTING_CASH, TARGET, SEED);

        System.out.printf("  %-5s %10s %12s %12s %10s%n", "DAY", "CASH", "HOLDINGS", "TOTAL", "RETURN");
        System.out.println("  " + "-".repeat(53));

        for (int day = 1; day <= DAYS; day++) {
            for (int hour = 0; hour < HOURS_PER_DAY; hour++) {
                int clockHour = 9 + hour;

                market.tick();
                runStrategy(market, portfolio, day, clockHour);
            }

            System.out.printf("  %-5d %10s %12s %12s %9.2f%%%n",
                    day,
                    money(portfolio.getCash()),
                    money(portfolio.getHoldingsValue()),
                    money(portfolio.getTotalValue()),
                    portfolio.getReturnPercent());
        }

        printFinalReport(market, portfolio);
    }

    /*
            the robots strategy - 3 rules
     */
    private static void runStrategy(Market market, Portfolio portfolio, int day, int hour) {

        // RULE 1 — Sell any position that has hit its profit target or stop loss
        //
        // Copy the collection first: selling modifies the portfolio's holdings
        // map, and modifying a collection while looping over it throws
        // ConcurrentModificationException. getHoldings() already returns a copy,
        // which is exactly why it does
        for (Holding holding : portfolio.getHoldings()) {
            double returnPercent = holding.getReturnPercent();

            if (returnPercent >= TAKE_PROFIT_PERCENT || returnPercent <= STOP_LOSS_PERCENT) {
                portfolio.sell(holding.getStock(), holding.getShares(), day, hour);
            }
        }

        // RULE 2 — Never hold more than three positions at once, so the robot
        //
        // does not simply buy everything and become the market index
        if (portfolio.getHoldings().size() >= 3) {
            return;
        }

        // RULE 3 — Buy the stock that has fallen furthest overall, betting it recovers
        //
        // This is a real strategy called "buying the dip", and on this
        // market it usually loses, which is a useful thing to see
        Stock target = null;
        double worstChange = 0;

        for (Stock stock : market.getStocks()) {
            if (portfolio.getSharesOwned(stock.getSymbol()) > 0) {
                continue;
            }
            if (stock.getTotalPercentChange() < worstChange) {
                worstChange = stock.getTotalPercentChange();
                target = stock;
            }
        }

        if (target == null) {
            return;
        }

        // Spend about a third of available cash, so the robot keeps a reserve
        int quantity = (int) (portfolio.getCash() / 3 / target.getCurrentPrice());
        if (quantity > 0) {
            portfolio.buy(target, quantity, day, hour);
        }
    }

    /*
            reporting
     */
    private static void printFinalReport(Market market, Portfolio portfolio) {

        System.out.println();
        System.out.println("  OPEN POSITIONS AT THE CLOSING BELL");
        if (portfolio.getHoldings().isEmpty()) {
            System.out.println("    (none, everything was sold)");
        } else {
            for (Holding holding : portfolio.getHoldings()) {
                System.out.println("    " + holding);
            }
        }

        System.out.println();
        System.out.println("  LAST FIVE TRADES");
        var transactions = portfolio.getTransactions();
        for (int i = Math.max(0, transactions.size() - 5); i < transactions.size(); i++) {
            System.out.println("    " + transactions.get(i));
        }

        // Count how many sales actually made money
        // This is the robot's hit rate
        long profitableSales = transactions.stream().filter(Transaction::isProfitable).count();
        long totalSales = transactions.stream().filter(t -> !t.isBuy()).count();

        System.out.println();
        System.out.println("  FINAL RESULT");
        System.out.printf("    Starting cash     %12s%n", money(portfolio.getStartingCash()));
        System.out.printf("    Final value       %12s%n", money(portfolio.getTotalValue()));
        System.out.printf("    Profit / loss     %12s%n", money(portfolio.getProfitLoss()));
        System.out.printf("    Return            %11.2f%%%n", portfolio.getReturnPercent());
        System.out.printf("    Market index      %11.2f%%%n", market.getMarketIndex());
        System.out.printf("    Trades made       %12d%n", portfolio.getTradeCount());
        System.out.printf("    Profitable sales  %9d / %d%n", profitableSales, totalSales);

        System.out.println();
        if (portfolio.getTotalValue() >= TARGET) {
            System.out.printf("    TARGET REACHED. %s beats %s.%n",
                    money(portfolio.getTotalValue()), money(TARGET));
        } else {
            System.out.printf("    Target missed. %s, short of %s.%n",
                    money(portfolio.getTotalValue()), money(TARGET));
        }

        // Beating the market is a different question from making money
        if (portfolio.getReturnPercent() > market.getMarketIndex()) {
            System.out.println("    The robot beat the market index.");
        } else {
            System.out.println("    The robot lost to the market index. Buying and holding");
            System.out.println("    everything would have done better than its strategy.");
        }
        System.out.println();
    }

    private static String money(double amount) {
        return String.format("$%,.2f", amount);
    }
}