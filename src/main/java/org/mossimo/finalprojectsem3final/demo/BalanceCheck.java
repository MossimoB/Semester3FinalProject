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
}
