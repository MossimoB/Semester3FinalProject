package org.mossimo.finalprojectsem3final.ui;

import org.mossimo.finalprojectsem3final.model.Transaction;
import org.mossimo.finalprojectsem3final.util.Formatters;

/**
 * A formatted snapshot of one completed trade
 */
public class TransactionRow {

    private final String when;
    private final String type;
    private final String symbol;
    private final String companyName;
    private final String shares;
    private final String pricePerShare;
    private final String total;
    private final String realised;

    private final boolean buy;
    private final double realisedProfit;

    /** Sorts newest first */
    private final int orderKey;

    public TransactionRow(Transaction transaction) {
        this.when = String.format("Day %d  %02d:00",
                transaction.getSimulationDay(), transaction.getSimulationHour());

        this.type = transaction.getType().name();
        this.symbol = transaction.getSymbol();
        this.companyName = transaction.getCompanyName();
        this.shares = Formatters.shares(transaction.getShares());
        this.pricePerShare = Formatters.money(transaction.getPricePerShare());
        this.total = Formatters.money(transaction.getTotal());

        this.buy = transaction.isBuy();
        this.realisedProfit = transaction.getRealisedProfit();
        this.orderKey = transaction.getOrderKey();

        // A purchase realises nothing, so an em dash rather than "+$0.00"
        // A column of meaningless zeros is worse than a column of blanks
        this.realised = transaction.isBuy()
                ? "–"
                : Formatters.signedMoney(transaction.getRealisedProfit());
    }

    public String getWhen() {
        return when;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getShares() {
        return shares;
    }

    public String getPricePerShare() {
        return pricePerShare;
    }

    public String getTotal() {
        return total;
    }

    public String getRealised() {
        return realised;
    }

    public boolean isBuy() {
        return buy;
    }

    public double getRealisedProfit() {
        return realisedProfit;
    }

    public int getOrderKey() {
        return orderKey;
    }

    /**
     * The CSS class for the realised column
     */
    public String getRealisedStyleClass() {
        if (buy) {
            return "neutral";
        }
        return Formatters.signClass(realisedProfit);
    }
}