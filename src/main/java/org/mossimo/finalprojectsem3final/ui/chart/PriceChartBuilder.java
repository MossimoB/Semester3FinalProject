package org.mossimo.finalprojectsem3final.ui.chart;

import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.util.StatisticsCalculator;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Owns the price chart for the currently selected company
 *
 * Draws two lines: the actual price, and a moving average that smooths out
 * hour-to-hour noise so the trend is visible
 */
public class PriceChartBuilder {
    /** How many hours the moving average covers. 8 is one trading day */
    private static final int MOVING_AVERAGE_WINDOW = 8;

    private final LineChart<Number, Number> chart;
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;

    private final XYChart.Series<Number, Number> priceSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> averageSeries = new XYChart.Series<>();

    /** The company currently drawn so we know when the selection changed */
    private String currentSymbol;

    public PriceChartBuilder() {

        xAxis = new NumberAxis();
        xAxis.setLabel("Simulated hours");
        // forceZeroInRange(false) lets the axis start wherever the data starts
        xAxis.setForceZeroInRange(false);
        xAxis.setMinorTickVisible(false);

        yAxis = new NumberAxis();
        yAxis.setLabel("Price");
        yAxis.setForceZeroInRange(false);
        yAxis.setMinorTickVisible(false);

        chart = new LineChart<>(xAxis, yAxis);

        // Animation off
        // It is designed for charts that change occasionally, and
        // it queues a transition per data point. Adding a point every 100ms at
        // 10x speed means the animations never finish and the line lags behind
        // the actual price
        chart.setAnimated(false);

        // Markers off: with 80 points they merge into a thick blurry band
        chart.setCreateSymbols(false);

        chart.setLegendVisible(true);

        priceSeries.setName("Price");
        averageSeries.setName(MOVING_AVERAGE_WINDOW + "h average");

        chart.getData().add(priceSeries);
        chart.getData().add(averageSeries);
    }


}
