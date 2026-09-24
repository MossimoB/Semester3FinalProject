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

    /**
     * Percentage change since the previous tick
     *
     *   percentChange = (current - previous) / previous × 100
     *
     * Shown in the "Change" column of the market table. Green above zero,
     * red below, which Week 6's {PercentTableCell} handles
     *
     * @return e.g. 2.4 for a 2.4% rise, -1.1 for a 1.1% fall
     */
    public double getPercentChange() {
        // Defensive: previousPrice can never be 0 because of MINIMUM_PRICE, but
        // dividing by zero produces Infinity rather than an exception,
        // and Infinity in a table cell is harder to diagnose than a 0
        if (previousPrice == 0) {
            return 0;
        }
        return (currentPrice - previousPrice) / previousPrice * 100.0;
    }

    /**
     * Percentage change since day 1
     *
     * Same formula as {#getPercentChange()}, measured from
     * {#startingPrice} instead of the last tick. This is the number that
     * tells the player whether a company has actually done well overall, rather
     * than whether it happened to twitch upward in the last hour
     */
    public double getTotalPercentChange() {
        if (startingPrice == 0) {
            return 0;
        }
        return (currentPrice - startingPrice) / startingPrice * 100.0;
    }

    /**
     * Dollar change since the previous tick. Positive for a rise
     *
     * Used by the stock detail header, which shows both the dollar
     * and the percentage move
     */
    public double getPriceChange() {
        return currentPrice - previousPrice;
    }

    /**
     * A plain-English risk label derived from volatility
     *
     * Shown on the stock detail screen so the player can judge a company
     * without having to interpret a decimal
     * The thresholds are arbitrary but consistent
     */
    public String getRiskLabel() {
        if (volatility < 0.030) {
            return "Low";
        }
        if (volatility < 0.050) {
            return "Medium";
        }
        if (volatility < 0.080) {
            return "High";
        }
        return "Very High";
    }

    /** How many prices are on record, always at least 1 */
    public int getHistorySize() {
        return priceHistory.size();
    }

    /*
            getters
     */
    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public String getDescription() {
        return description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getVolatility() {
        return volatility;
    }

    public double getTrend() {
        return trend;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    /**
     * The price history, oldest first
     */
    public List<Double> getPriceHistory() {
        return List.copyOf(priceHistory);
    }

    /*
            object methods
     */

    @Override
    public String toString() {
        return String.format("%-4s %-10s $%8.2f  %+6.2f%%",
                symbol, companyName, currentPrice, getPercentChange());
    }

    /**
     * Two Stock objects are the same stock if they have the same symbol
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Stock)) {
            return false;
        }
        return symbol.equals(((Stock) other).symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }
}
