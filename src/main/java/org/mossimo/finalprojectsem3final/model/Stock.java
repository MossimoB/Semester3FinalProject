package org.mossimo.finalprojectsem3final.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A single fictional company's stock
 *
 * Holds identity (symbol, name, industry), the two settings that control how
 * it moves ({volatility} and {trend}), its current and previous
 * price, and the full list of every price it has had
 *
 * All money values are plain {double} dollars. That is accurate enough
 * for a simulation
 */
public class Stock {
    /*
            constants
     */

    /**
     * A share can never be worth less than this
     */
    public static final double MINIMUM_PRICE = 1.00;

    /** Short ticker code shown in the market table */
    private final String symbol;

    /** Full company name, e.g. "NovaTech" */
    private final String companyName;

    /** Industry sector, e.g. "Technology" shown on the stock detail screen */
    private final String industry;

    /** One line of flavour text for the stock detail screen */
    private final String description;


    /** The price this stock opened at on day 1. Used for "change overall" */
    private final double startingPrice;

    /**
     * How violently this stock moves, as the standard deviation of its hourly
     * percentage change
     */
    private final double volatility;

    /**
     * Long-run drift per hour, as a fraction
     *
     * 0.002 means this company gains about 0.2% per simulated hour on
     * average, before randomness. A negative trend is a company in decline.
     * This is what makes some companies worth holding and others worth
     * avoiding
     */
    private final double trend;

    private double currentPrice;
    private double previousPrice;

    /**
     * Every price this stock has ever had, oldest first
     */
    private final List<Double> priceHistory = new ArrayList<>();

    /**
     * Creates a stock sitting at its starting price with a one-entry history
     *
     * @param symbol        ticker code
     * @param companyName   display name
     * @param industry      sector
     * @param description   one line of flavour text
     * @param startingPrice opening price on day 1, in dollars
     * @param volatility    standard deviation of hourly percentage change
     * @param trend         average drift per hour, as a fraction
     */
    public Stock(String symbol,
                 String companyName,
                 String industry,
                 String description,
                 double startingPrice,
                 double volatility,
                 double trend) {

        this.symbol = symbol;
        this.companyName = companyName;
        this.industry = industry;
        this.description = description;
        this.startingPrice = startingPrice;
        this.volatility = volatility;
        this.trend = trend;

        // A brand-new stock has not moved yet, so current and previous are the same
        // That makes getPercentChange() return 0 instead of dividing by
        // a price that does not exist
        this.currentPrice = startingPrice;
        this.previousPrice = startingPrice;

        // The history always starts with one entry
        // An empty history would make the chart and every statistic a special case
        this.priceHistory.add(startingPrice);
    }

    /**
     * Moves this stock to a new price
     *
     * Three things happen, in this order:
     *      The current price becomes the previous price
     *      The new price is stored, floored at {#MINIMUM_PRICE}
     *      The new price is appended to the history
     *
     * @param newPrice the price before flooring, in dollars
     */
    public void setPrice(double newPrice) {
        this.previousPrice = this.currentPrice;
        this.currentPrice = Math.max(MINIMUM_PRICE, newPrice);
        this.priceHistory.add(this.currentPrice);
    }



}
