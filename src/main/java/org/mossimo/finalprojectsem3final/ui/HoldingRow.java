package org.mossimo.finalprojectsem3final.ui;

import org.mossimo.finalprojectsem3final.model.Holding;

/**
 * A read-only snapshot of one position, shaped for the portfolio table
 */
public class HoldingRow {

    private final String symbol;
    private final String companyName;
    private final int shares;
    private final double averageCost;
    private final double currentPrice;
    private final double currentValue;
    private final double costBasis;
    private final double profitLoss;
    private final double returnPercent;
    private final double breakEvenMovePercent;

    /**
     * What fraction of the whole account this position represents, 0 to 100
     */
    private final double weightPercent;

    public HoldingRow(Holding holding, double portfolioTotalValue) {
        this.symbol = holding.getSymbol();
        this.companyName = holding.getStock().getCompanyName();
        this.shares = holding.getShares();
        this.averageCost = holding.getAverageCost();
        this.currentPrice = holding.getStock().getCurrentPrice();
        this.currentValue = holding.getCurrentValue();
        this.costBasis = holding.getCostBasis();
        this.profitLoss = holding.getProfitLoss();
        this.returnPercent = holding.getReturnPercent();
        this.breakEvenMovePercent = holding.getBreakEvenMovePercent();

        this.weightPercent = (portfolioTotalValue == 0)
                ? 0
                : currentValue / portfolioTotalValue * 100.0;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getShares() {
        return shares;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public double getCostBasis() {
        return costBasis;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public double getReturnPercent() {
        return returnPercent;
    }

    public double getBreakEvenMovePercent() {
        return breakEvenMovePercent;
    }

    public double getWeightPercent() {
        return weightPercent;
    }
}