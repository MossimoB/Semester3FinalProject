package org.mossimo.finalprojectsem3final.model;

import org.mossimo.finalprojectsem3final.util.PriceGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** * The collection of tradeable companies, plus the engine that moves them */
public class Market {
    /** The companies in the order they appear in the market table */
    private final List<Stock> stocks = new ArrayList<>();

    /** Shared source of randomness */
    private final PriceGenerator priceGenerator;

    public Market(PriceGenerator priceGenerator) {
        this.priceGenerator = priceGenerator;
    }

    /**
     * Builds the standard six-company market described in our proposal
     *
     * The numbers below are the whole game design in one table. Read them as
     * a set of trade-offs offered to the player:
     *
     *
     *   Symbol  Company     Start   Volatility  Trend     The pitch to the player
     *   ------  ---------   ------  ----------  --------  ----------------------------
     *   NVT     NovaTech    $100     0.060      +0.0020   Solid grower, real swings
     *   GRG     GreenGrid    $75     0.040      +0.0015   Steady, unexciting, reliable
     *   MDC     MedCore      $60     0.020      +0.0010   The safe place to park cash
     *   AER     AeroWorks   $120     0.055      -0.0005   Expensive and slowly sinking
     *   FRB     FreshBite    $40     0.020      +0.0008   Cheap, dull, hard to lose on
     *   QAI     QuantumAI   $150     0.100      +0.0030   The gamble. Best and worst.
     *
     *
     * AeroWorks has a negative trend deliberately. Without at least one
     * company that loses money over time, "buy everything and wait" is a winning
     * strategy and the game has no decisions in it
     *
     * QuantumAI has the highest trend AND the highest volatility, so over ten
     * days it is usually either the best or the worst performer. That is the
     * risk/reward trade-off the whole simulation is built around
     */
    public static Market createDefaultMarket(PriceGenerator priceGenerator) {
        return createDefaultMarket(priceGenerator, 1.0);
    }

    /**
     * Builds the standard market with every volatility scaled by a multiplier.
     *
     * Easy passes 0.75 for a calmer market
     * Hard passes 1.40 for a wilder one
     *
     * The relative risk between companies is preserved:
     * QuantumAI stays five times as volatile as MedCore at every difficulty
     * Only the overall intensity changes
     *
     * @param volatilityMultiplier 1.0 for the standard market
     */
    public static Market createDefaultMarket(PriceGenerator priceGenerator,
                                             double volatilityMultiplier) {
        Market market = new Market(priceGenerator);
        double m = volatilityMultiplier;

        market.addStock(new Stock("NVT", "NovaTech", "Technology",
                "Consumer electronics and cloud services. Grows well, moves hard.",
                100.00, 0.060 * m, 0.0020));

        market.addStock(new Stock("GRG", "GreenGrid", "Renewable Energy",
                "Wind and solar infrastructure. Steady contracts, steady returns.",
                75.00, 0.040 * m, 0.0015));

        market.addStock(new Stock("MDC", "MedCore", "Healthcare",
                "Hospital equipment and diagnostics. The calmest stock on the board.",
                60.00, 0.020 * m, 0.0010));

        market.addStock(new Stock("AER", "AeroWorks", "Aerospace",
                "Aircraft components. Expensive, and losing ground to competitors.",
                120.00, 0.055 * m, -0.0005));

        market.addStock(new Stock("FRB", "FreshBite", "Food",
                "Packaged food and drinks. Cheap, dull, and hard to lose money on.",
                40.00, 0.020 * m, 0.0008));

        market.addStock(new Stock("QAI", "QuantumAI", "Artificial Intelligence",
                "Experimental AI research. The biggest gamble on the market.",
                150.00, 0.100 * m, 0.0030));

        return market;
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    /*
            the tick
     */
    /**
     * Moves every stock forward one simulated hour
     *
     * Called once per tick
     *
     * Every stock moves independently. There is no correlation between them,
     * which is a simplification: in a real market, bad news for one aerospace
     * company tends to drag the whole sector down. The future news market-wide boom
     * and crash events are how this project approximates that
     *
     * * * Ww should mention this in our presentation as a known limitation
     *
     * @return the percentage change applied to each stock, in the same order as {#getStocks()}
     */
    public List<Double> tick() {
        List<Double> changes = new ArrayList<>(stocks.size());
        for (Stock stock : stocks) {
            changes.add(priceGenerator.tick(stock));
        }
        return changes;
    }

    /*
            lookups and statistics
     */
    /** Finds a company by its ticker symbol */
    public Optional<Stock> findBySymbol(String symbol) {
        return stocks.stream()
                .filter(stock -> stock.getSymbol().equals(symbol))
                .findFirst();
    }

    /** The company that has gained the most since day 1 */
    public Optional<Stock> getBestPerformer() {
        return stocks.stream()
                .max(Comparator.comparingDouble(Stock::getTotalPercentChange));
    }

    /** The company that has lost the most since day 1 */
    public Optional<Stock> getWorstPerformer() {
        return stocks.stream()
                .min(Comparator.comparingDouble(Stock::getTotalPercentChange));
    }

    /**
     * The market index: the average total percentage change across all companies
     *
     * This is the number the player is competing against
     * If the index is +18% and their portfolio is up 12%, they picked badly even though they
     * made money. The results screen uses this to decide whether the
     * player genuinely "beat the market" or merely rode it upward
     */
    public double getMarketIndex() {
        return stocks.stream()
                .mapToDouble(Stock::getTotalPercentChange)
                .average()
                .orElse(0.0);
    }

    /** The average percentage change across all companies in the last tick only */
    public double getLastTickAverageChange() {
        return stocks.stream()
                .mapToDouble(Stock::getPercentChange)
                .average()
                .orElse(0.0);
    }

    /*
            getters
     */
    /** The companies in the order they're displayed */
    public List<Stock> getStocks() {
        return List.copyOf(stocks);
    }

    /** How many companies are on the market */
    public int size() {
        return stocks.size();
    }

    public PriceGenerator getPriceGenerator() {
        return priceGenerator;
    }

    /** A one-line summary for the console demos */
    @Override
    public String toString() {
        return String.format("Market of %d companies, index %+.2f%%",
                stocks.size(), getMarketIndex());
    }
}
