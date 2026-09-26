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

    /** The chart node, to be put into a layout */
    public LineChart<Number, Number> getChart() {
        return chart;
    }

    /*
            drawing
     */
    /**
     * Shows a company's chart, redrawing completely only if it changed
     */
    public void show(Stock stock) {
        if (stock == null) {
            clear();
            return;
        }

        if (!stock.getSymbol().equals(currentSymbol)) {
            currentSymbol = stock.getSymbol();
            redraw(stock);
        } else {
            appendLatest(stock);
        }
    }

    /** Rebuilds both lines from scratch. Used when the selection changes */
    private void redraw(Stock stock) {
        priceSeries.getData().clear();
        averageSeries.getData().clear();

        List<Double> history = stock.getPriceHistory();
        List<Double> averages = StatisticsCalculator.movingAverageSeries(history, MOVING_AVERAGE_WINDOW);

        for (int i = 0; i < history.size(); i++) {
            priceSeries.getData().add(new XYChart.Data<>(i, history.get(i)));
            averageSeries.getData().add(new XYChart.Data<>(i, averages.get(i)));
        }

        yAxis.setLabel("Price  ·  " + stock.getSymbol());
    }

    /**
     * Adds only the points that are new since the last call
     */
    private void appendLatest(Stock stock) {
        List<Double> history = stock.getPriceHistory();
        int drawn = priceSeries.getData().size();

        if (drawn == history.size()) {
            return;                       // nothing new, the common case
        }

        if (drawn > history.size()) {
            // The history got shorter, which only happens after a Reset or a
            // loaded save
            // Start over
            redraw(stock);
            return;
        }

        List<Double> averages = StatisticsCalculator.movingAverageSeries(history, MOVING_AVERAGE_WINDOW);

        for (int i = drawn; i < history.size(); i++) {
            priceSeries.getData().add(new XYChart.Data<>(i, history.get(i)));
            averageSeries.getData().add(new XYChart.Data<>(i, averages.get(i)));
        }
    }

    /** Empties the chart
     * Used when nothing is selected */
    public void clear() {
        priceSeries.getData().clear();
        averageSeries.getData().clear();
        currentSymbol = null;
        yAxis.setLabel("Price");
    }

    /** Forces a full redraw on the next {#show}
     * Used after a Reset */
    public void invalidate() {
        currentSymbol = null;
    }
}
