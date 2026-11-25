package org.mossimo.finalprojectsem3final.demo;

import org.mossimo.finalprojectsem3final.model.Market;
import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.util.PriceGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BalanceCheck {
    /*
            settings
     */
    /** Ticks in one complete game: 10 days × 8 trading hours */
    private static final int TICKS_PER_GAME = 80;

    /** How many complete games to simulate. 3000 takes about 10 seconds */
    private static final int RUNS = 3000;

    /** The target the player is trying to hit as a percentage gain */
    private static final double TARGET_PERCENT = 50.0;

    /*
            main
     */
    public static void main(String[] args) {

        System.out.println();
        System.out.println("  STOCKSIM  ·  BALANCE CHECK");
        System.out.printf("  Simulating %,d complete games of %d ticks each...%n%n",
                RUNS, TICKS_PER_GAME);

        List<Double> indexResults = new ArrayList<>(RUNS);
        List<Double> bestStockResults = new ArrayList<>(RUNS);
        int gamesWonByIndexAlone = 0;

        // One list of outcomes per company, so we can report each separately.
        int companyCount = Market.createDefaultMarket(new PriceGenerator(0)).size();
        List<List<Double>> perCompany = new ArrayList<>();
        for (int i = 0; i < companyCount; i++) {
            perCompany.add(new ArrayList<>(RUNS));
        }

        // Run the games
        // Seeds 0, 1, 2, ... are used so this tool gives the same answer every time it runs
        // A tuning tool whose output moves around is useless for deciding whether a change helped
        for (int seed = 0; seed < RUNS; seed++) {

            Market market = Market.createDefaultMarket(new PriceGenerator(seed));

            for (int tick = 0; tick < TICKS_PER_GAME; tick++) {
                market.tick();
            }

            indexResults.add(market.getMarketIndex());
            bestStockResults.add(market.getBestPerformer().orElseThrow().getTotalPercentChange());

            if (market.getMarketIndex() >= TARGET_PERCENT) {
                gamesWonByIndexAlone++;
            }

            List<Stock> stocks = market.getStocks();
            for (int i = 0; i < stocks.size(); i++) {
                perCompany.get(i).add(stocks.get(i).getTotalPercentChange());
            }
        }

        // Report
        printDistribution("MARKET INDEX", indexResults);
        printDistribution("BEST STOCK  ", bestStockResults);

        System.out.printf("%n  Buying the whole market and waiting reaches +%.0f%% in %.1f%% of games.%n",
                TARGET_PERCENT, 100.0 * gamesWonByIndexAlone / RUNS);
        System.out.println("  (If this were above about 25%, the game would be too easy:");
        System.out.println("   the player could win without making a single real decision.)");

        System.out.println();
        System.out.println("  PER COMPANY");
        System.out.printf("  %-5s %-12s %10s %10s %10s %10s%n",
                "SYM", "COMPANY", "MEAN", "MEDIAN", "P10", "P90");
        System.out.println("  " + "-".repeat(61));

        Market labels = Market.createDefaultMarket(new PriceGenerator(0));
        for (int i = 0; i < companyCount; i++) {
            Stock stock = labels.getStocks().get(i);
            List<Double> results = perCompany.get(i);
            Collections.sort(results);

            System.out.printf("  %-5s %-12s %+9.2f%% %+9.2f%% %+9.2f%% %+9.2f%%%n",
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    mean(results),
                    percentile(results, 0.50),
                    percentile(results, 0.10),
                    percentile(results, 0.90));
        }

        System.out.println();
        System.out.println("  LOOK FOR A COMPANY WHOSE MEAN IS HIGH BUT MEDIAN IS NEGATIVE.");
        System.out.println("  That is volatility drag.");
        System.out.println("  it is the strongest piece of mathematics in the project.");
        System.out.println();
    }

    /*
            statistics helpers
     */
    private static void printDistribution(String label, List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        System.out.printf("  %s   mean %+7.2f%%   median %+7.2f%%   p10 %+7.2f%%   p90 %+7.2f%%%n",
                label,
                mean(sorted),
                percentile(sorted, 0.50),
                percentile(sorted, 0.10),
                percentile(sorted, 0.90));
    }

    /** The arithmetic mean: add everything up then divide by how many there are */
    private static double mean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /**
     * The value below which the given fraction of results fall
     *
     * The list must already be sorted. This uses the simple nearest-rank
     * method rather than interpolating between neighbours
     */
    private static double percentile(List<Double> sortedValues, double fraction) {
        if (sortedValues.isEmpty()) {
            return 0;
        }
        int index = (int) (sortedValues.size() * fraction);
        index = Math.min(index, sortedValues.size() - 1);
        return sortedValues.get(index);
    }
}
