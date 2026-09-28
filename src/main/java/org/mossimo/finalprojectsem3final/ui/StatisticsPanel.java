package org.mossimo.finalprojectsem3final.ui;

import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.util.Formatters;
import org.mossimo.finalprojectsem3final.util.StatisticsCalculator;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

import java.util.List;

/** Eight statistics about the selected company, in a four-by-two grid */
public class StatisticsPanel extends GridPane {

    private static final int COLUMNS = 4;

    private final Label highValue = new Label("—");
    private final Label lowValue = new Label("—");
    private final Label meanValue = new Label("—");
    private final Label medianValue = new Label("—");
    private final Label stdDevValue = new Label("—");
    private final Label cvValue = new Label("—");
    private final Label drawdownValue = new Label("—");
    private final Label rangeValue = new Label("—");

    public StatisticsPanel() {
        setHgap(18);
        setVgap(8);
        setPadding(new Insets(10, 0, 4, 0));

        // Four equal columns so the grid does not shuffle sideways as the numbers change width
        for (int i = 0; i < COLUMNS; i++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(100.0 / COLUMNS);
            constraints.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(constraints);
        }

        addStat(0, 0, "HIGH", highValue,
                "The highest price this company has reached so far.");
        addStat(1, 0, "LOW", lowValue,
                "The lowest price this company has reached so far.");
        addStat(2, 0, "MEAN", meanValue,
                "The average of every price so far: sum of prices divided by how many there are.");
        addStat(3, 0, "MEDIAN", medianValue,
                "The middle price once they are sorted. Unlike the mean, a single "
                        + "spike cannot drag it far, so it shows the typical price.");

        addStat(0, 1, "STD DEV", stdDevValue,
                "Standard deviation: how far prices typically sit from the mean. "
                        + "The square root of the average squared distance from the mean.");
        addStat(1, 1, "VOLATILITY", cvValue,
                "Standard deviation as a percentage of the mean. This is what lets "
                        + "you compare risk between a $150 stock and a $40 one.");
        addStat(2, 1, "MAX DROP", drawdownValue,
                "The worst peak-to-trough fall so far. What you would have lost by "
                        + "buying at the best moment and selling at the worst one after it.");
        addStat(3, 1, "RANGE", rangeValue,
                "The distance between the highest and lowest price.");
    }

    private void addStat(int column, int row, String caption, Label valueLabel, String explanation) {
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("stat-label");

        valueLabel.getStyleClass().addAll("stat-value", "mono");

        Tooltip tooltip = new Tooltip(explanation);
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(280);
        // The default delay is nearly a second, which feels broken. Tooltips
        // that explain a term should appear as soon as the player looks.
        tooltip.setShowDelay(Duration.millis(250));

        Tooltip.install(captionLabel, tooltip);
        Tooltip.install(valueLabel, tooltip);

        javafx.scene.layout.VBox cell = new javafx.scene.layout.VBox(1, captionLabel, valueLabel);
        add(cell, column, row);
    }

    /**
     * Recalculates every statistic from the company's price history.
     */
    public void update(Stock stock) {
        if (stock == null) {
            clear();
            return;
        }

        List<Double> history = stock.getPriceHistory();

        highValue.setText(Formatters.money(StatisticsCalculator.max(history)));
        lowValue.setText(Formatters.money(StatisticsCalculator.min(history)));
        meanValue.setText(Formatters.money(StatisticsCalculator.mean(history)));
        medianValue.setText(Formatters.money(StatisticsCalculator.median(history)));
        stdDevValue.setText(Formatters.money(StatisticsCalculator.standardDeviation(history)));
        rangeValue.setText(Formatters.money(StatisticsCalculator.range(history)));

        cvValue.setText(Formatters.plainPercent(
                StatisticsCalculator.coefficientOfVariation(history)));

        double drawdown = StatisticsCalculator.maxDrawdownPercent(history);
        drawdownValue.setText(Formatters.plainPercent(drawdown));

        // A drawdown over 25% is worth flagging: it means this company has
        // already lost a quarter of its value once
        drawdownValue.getStyleClass().removeAll("loss", "warning", "neutral");
        drawdownValue.getStyleClass().add(
                drawdown > 25 ? "loss" : drawdown > 10 ? "warning" : "neutral");
    }

    private void clear() {
        for (Label label : List.of(highValue, lowValue, meanValue, medianValue,
                stdDevValue, cvValue, drawdownValue, rangeValue)) {
            label.setText("—");
        }
    }
}
