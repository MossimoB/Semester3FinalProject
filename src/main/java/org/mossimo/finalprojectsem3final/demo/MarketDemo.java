package org.mossimo.finalprojectsem3final.demo;

import org.mossimo.finalprojectsem3final.model.Market;
import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.util.PriceGenerator;

/**
 * A console program that runs the market for ten simulated days and prints it
 */
public class MarketDemo {

    /*
            settings
     */
    /**
     * Simulated trading hours per day. 09:00 to 16:00 inclusive is 8.
     */
    private static final int HOURS_PER_DAY = 8;

    /**
     * How many days to run.
     */
    private static final int DAYS = 10;

    /**
     * Set to a fixed number to replay the exact same market every run, which is
     * what we want while tuning
     * <p>
     * Seed 84 is used because it is typical.
     * {BalanceCheck} measured 3000 markets and found the median
     * index lands near +7%; seed 84 gives +6.69%, with one clear winner
     * (NovaTech, +76%) and one clear disaster (AeroWorks, -59%)
     * <p>
     * This matters more than it sounds. The first seed tried here was 42,
     * which produced a +47% index and made the game look trivially winnable.
     * It was an outlier
     */
    private static final Long SEED = 84L;

    /**
     * ANSI colour codes make gains green and losses red in the terminal
     */
    private static final boolean USE_COLOUR = true;

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String DIM = "\u001B[90m";
    private static final String BOLD = "\u001B[1m";
    private static final String RESET = "\u001B[0m";

    /*
            main
     */
    public static void main(String[] args) {

        // Build the market. This is the same call Week 4's Simulation will make.
        PriceGenerator generator = (SEED == null)
                ? new PriceGenerator()
                : new PriceGenerator(SEED);

        Market market = Market.createDefaultMarket(generator);

        printBanner();
        printOpeningPrices(market);

        // Run the clock. Each inner loop is one simulated hour.
        for (int day = 1; day <= DAYS; day++) {
            for (int hour = 0; hour < HOURS_PER_DAY; hour++) {
                market.tick();
            }
            printDayRow(day, market);
        }

        printSummary(market);
    }

    /*
            output
     */
    private static void printBanner() {
        System.out.println();
        System.out.println(bold("  STOCKSIM  ·  MARKET DEMO"));
        System.out.println(dim("  Week 2. No interface yet. This is the price model on its own."));
        System.out.println(dim("  Seed: " + (SEED == null ? "random (a new market every run)" : SEED)));
        System.out.println();
    }

    /**
     * Prints each company's starting position, so you can see what the player is
     * choosing between before anything moves
     */
    private static void printOpeningPrices(Market market) {
        System.out.println(bold("  OPENING PRICES"));
        System.out.printf("  %-5s %-12s %-24s %10s %8s %10s%n",
                "SYM", "COMPANY", "INDUSTRY", "PRICE", "RISK", "TREND/HR");
        System.out.println(dim("  " + "-".repeat(73)));

        for (Stock stock : market.getStocks()) {
            System.out.printf("  %-5s %-12s %-24s %10s %8s %9.2f%%%n",
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    stock.getIndustry(),
                    money(stock.getCurrentPrice()),
                    stock.getRiskLabel(),
                    stock.getTrend() * 100);
        }

        System.out.println();
        System.out.println(bold("  DAILY CLOSING PRICES"));
        System.out.printf("  %-6s", "DAY");
        for (Stock stock : market.getStocks()) {
            System.out.printf(" %11s", stock.getSymbol());
        }
        System.out.printf(" %11s%n", "INDEX");
        System.out.println(dim("  " + "-".repeat(6 + 12 * (market.size() + 1))));
    }

    /**
     * One row per simulated day: every closing price plus the market index
     */
    private static void printDayRow(int day, Market market) {
        System.out.printf("  %-6s", day);

        for (Stock stock : market.getStocks()) {
            // Total change since day 1 decides the colour, so a column that is
            // red all the way down is a company that never recovered
            String text = money(stock.getCurrentPrice());
            System.out.printf(" %s", pad(colour(text, stock.getTotalPercentChange()), 11));
        }

        double index = market.getMarketIndex();
        System.out.printf(" %s%n", pad(colour(String.format("%+.2f%%", index), index), 11));
    }

    /**
     * The closing report: who won, who lost, and by how much
     */
    private static void printSummary(Market market) {
        System.out.println();
        System.out.println(bold("  AFTER " + DAYS + " DAYS (" + (DAYS * HOURS_PER_DAY) + " ticks)"));
        System.out.printf("  %-5s %-12s %10s %10s %11s %10s %10s%n",
                "SYM", "COMPANY", "START", "FINAL", "CHANGE", "HIGH", "LOW");
        System.out.println(dim("  " + "-".repeat(73)));

        for (Stock stock : market.getStocks()) {
            double change = stock.getTotalPercentChange();

            // High and low are calculated here by hand
            double high = stock.getPriceHistory().stream()
                    .mapToDouble(Double::doubleValue).max().orElse(0);
            double low = stock.getPriceHistory().stream()
                    .mapToDouble(Double::doubleValue).min().orElse(0);

            System.out.printf("  %-5s %-12s %10s %10s %s %10s %10s%n",
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    money(stock.getStartingPrice()),
                    money(stock.getCurrentPrice()),
                    pad(colour(String.format("%+.2f%%", change), change), 11),
                    money(high),
                    money(low));
        }

        System.out.println();

        Stock best = market.getBestPerformer().orElseThrow();
        Stock worst = market.getWorstPerformer().orElseThrow();

        System.out.printf("  Best performer   %s  %s%n",
                pad(best.getCompanyName(), 14),
                colour(String.format("%+.2f%%", best.getTotalPercentChange()),
                        best.getTotalPercentChange()));

        System.out.printf("  Worst performer  %s  %s%n",
                pad(worst.getCompanyName(), 14),
                colour(String.format("%+.2f%%", worst.getTotalPercentChange()),
                        worst.getTotalPercentChange()));

        System.out.printf("  Market index     %s  %s%n",
                pad("all companies", 14),
                colour(String.format("%+.2f%%", market.getMarketIndex()),
                        market.getMarketIndex()));

        System.out.println();
        System.out.println(dim("  Is this interesting? If every column climbed steadily and nothing"));
        System.out.println(dim("  surprised you, raise the volatility numbers in Market and run again."));
        System.out.println(dim("  Tuning is cheap now and expensive in Week 9."));
        System.out.println();
    }

    /*
            small formatting helpers
     */
    /** Formats a number as dollars: 1234.5 becomes "$1,234.50" */
    private static String money(double amount) {
        return String.format("$%,.2f", amount);
    }

    /** Green for positive, red for negative, plain for zero */
    private static String colour(String text, double value) {
        if (!USE_COLOUR) {
            return text;
        }
        if (value > 0) {
            return GREEN + text + RESET;
        }
        if (value < 0) {
            return RED + text + RESET;
        }
        return text;
    }

    private static String bold(String text) {
        return USE_COLOUR ? BOLD + text + RESET : text;
    }

    private static String dim(String text) {
        return USE_COLOUR ? DIM + text + RESET : text;
    }

    /** Right-aligns text in a fixed width and ignoring invisible colour codes */
    private static String pad(String text, int width) {
        int visibleLength = text.replaceAll("\u001B\\[[0-9;]*m", "").length();
        int padding = Math.max(0, width - visibleLength);
        return " ".repeat(padding) + text;
    }
}