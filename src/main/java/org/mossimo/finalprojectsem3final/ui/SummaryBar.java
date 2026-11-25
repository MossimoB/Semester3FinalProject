package org.mossimo.finalprojectsem3final.ui;

import org.mossimo.finalprojectsem3final.model.Portfolio;
import org.mossimo.finalprojectsem3final.model.Simulation;
import org.mossimo.finalprojectsem3final.util.Formatters;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SummaryBar extends HBox {

    private final Label cashValue = new Label("-");
    private final Label holdingsValue = new Label("-");
    private final Label totalValue = new Label("-");
    private final Label profitValue = new Label("-");
    private final Label targetValue = new Label("-");

    public SummaryBar() {
        setSpacing(10);
        setPadding(new Insets(0, 0, 12, 0));
        getStyleClass().add("summary-bar");

        getChildren().addAll(
                buildCell("CASH", cashValue),
                buildCell("PORTFOLIO", holdingsValue),
                buildCell("TOTAL VALUE", totalValue),
                buildCell("PROFIT / LOSS", profitValue),
                buildCell("TARGET", targetValue));
    }

    private VBox buildCell(String caption, Label valueLabel) {
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("summary-caption");

        valueLabel.getStyleClass().addAll("summary-value", "mono");

        VBox box = new VBox(2, captionLabel, valueLabel);
        box.getStyleClass().add("summary-cell");
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    public void update(Simulation simulation) {
        Portfolio portfolio = simulation.getPortfolio();

        cashValue.setText(Formatters.money(portfolio.getCash()));
        holdingsValue.setText(Formatters.money(portfolio.getHoldingsValue()));
        totalValue.setText(Formatters.money(portfolio.getTotalValue()));
        targetValue.setText(Formatters.money(simulation.getTargetValue()));

        double profit = portfolio.getProfitLoss();
        profitValue.setText(String.format("%s  (%s)",
                Formatters.signedMoney(profit),
                Formatters.percent(portfolio.getReturnPercent())));

        profitValue.getStyleClass().removeAll("gain", "loss", "neutral");
        profitValue.getStyleClass().add(Formatters.signClass(profit));

        totalValue.getStyleClass().removeAll("gain", "neutral");
        totalValue.getStyleClass().add(
                portfolio.getTotalValue() >= simulation.getTargetValue() ? "gain" : "neutral");
    }
}
