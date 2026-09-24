package org.mossimo.finalprojectsem3final.demo;

import org.mossimo.finalprojectsem3final.model.Holding;
import org.mossimo.finalprojectsem3final.model.Market;
import org.mossimo.finalprojectsem3final.model.Portfolio;
import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.model.TradeResult;
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
            the robot's strategy
     */

}
