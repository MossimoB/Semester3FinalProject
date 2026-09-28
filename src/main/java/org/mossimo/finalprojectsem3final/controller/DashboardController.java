package org.mossimo.finalprojectsem3final.controller;

import org.mossimo.finalprojectsem3final.app.AppContext;
import org.mossimo.finalprojectsem3final.app.SceneManager;
import org.mossimo.finalprojectsem3final.app.ScreenId;
// import org.mossimo.finalprojectsem3final.model.Game;
// import org.mossimo.finalprojectsem3final.model.Simulation;
import org.mossimo.finalprojectsem3final.model.Stock;
// import org.mossimo.finalprojectsem3final.ui.ClockSpeed;
import org.mossimo.finalprojectsem3final.ui.MarketTableFactory;
// import org.mossimo.finalprojectsem3final.ui.SimulationClock;
import org.mossimo.finalprojectsem3final.ui.StatisticsPanel;
// import org.mossimo.finalprojectsem3final.ui.SummaryBar;
import org.mossimo.finalprojectsem3final.ui.chart.PriceChartBuilder;
import org.mossimo.finalprojectsem3final.util.Formatters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

/*
I will also overdo the comments in this file
 */
public class DashboardController implements SceneManager.ScreenController {

/*
        FXML fields
Each name must match an fx:id in dashboard.fxml exactly, including case
 */
@FXML private Label difficultyLabel;
    @FXML private Label clockLabel;
    @FXML private Label stateLabel;
    @FXML private ProgressBar progressBar;

    @FXML private Button playPauseButton;
    @FXML private Button speedButton;
    @FXML private Button portfolioButton;
    @FXML private Button saveButton;
    @FXML private Button menuButton;

    @FXML private SummaryBar summaryBar;
    @FXML private TableView<Stock> marketTable;
    @FXML private Label marketIndexLabel;

    @FXML private Label detailNameLabel;
    @FXML private Label riskBadge;
    @FXML private Label detailIndustryLabel;
    @FXML private Label detailPriceLabel;
    @FXML private Label detailChangeLabel;
    @FXML private Label detailDescriptionLabel;
    @FXML private StackPane chartContainer;
    @FXML private StatisticsPanel statisticsPanel;

    private AppContext context;
    private Game game;
    private Simulation simulation;
    private SimulationClock clock;

    /** Owns the price chart */
    private final PriceChartBuilder priceChart = new PriceChartBuilder();
    private final ObservableList<Stock> marketRows = FXCollections.observableArrayList();

    /**
     * Runs while the FXML loads
     * The context does not exist yet
     * so nothing here may touch it
     */
    @FXML
    public void initialize(){
        MarketTableFactory.configure(marketTable);
        marketTable.setItems(marketRows);

        // no clock to control yet
        playPauseButton.setDisable(true);
        speedButton.setDisable(true);
    }

    /**
     * Runs after the FXML has landed
     * The context exists from here on
     */
    @Override
    public void init(AppContext context) {
        this.context = context;
        this.game = context.startNewGame();
        this.simulation = game.getSimulation();

        difficultyLabel.setText(game.getConfig().getDifficulty().getDisplayName());

        marketRows.setAll(game.getMarket().getStocks());
        marketTable.getSelectionModel().selectFirst();

        clock = new SimulationClock(simulation);
        clock.setOnTick(this::refresh);
        clock.setOnFinished(this::onGameFinished);

        speedButton.setText(clock.getSpeed().getLabel());

        refresh();
    }

    /** Updates every part of the screen from the current model state */
    private void refresh() {
        clockLabel.setText(simulation.getClockLabel());
        stateLabel.setText(simulation.getStateLabel());
        progressBar.setProgress(simulation.getProgress());

        summaryBar.update(simulation);

        marketIndexLabel.setText("Market index "
                + Formatters.percent(game.getMarket().getMarketIndex()));

        marketTable.refresh();

        showSelectedStock(marketTable.getSelectionModel().getSelectedItem());
    }

    /** Fills in the right-hand panel for one company */
    private void showSelectedStock(Stock stock) {
        if (stock == null) {
            detailNameLabel.setText("Select a company");
            detailIndustryLabel.setText("");
            detailPriceLabel.setText("—");
            detailChangeLabel.setText("");
            detailDescriptionLabel.setText("");
            riskBadge.setText("");
            priceChart.clear();
            statisticsPanel.update(null);
            return;
        }

        detailNameLabel.setText(stock.getCompanyName());
        detailIndustryLabel.setText(stock.getIndustry() + " · " + stock.getSymbol());
        detailPriceLabel.setText(Formatters.money(stock.getCurrentPrice()));
        detailDescriptionLabel.setText(stock.getDescription());

        detailChangeLabel.setText(String.format("%s today     %s overall",
                Formatters.percent(stock.getPercentChange()),
                Formatters.percent(stock.getTotalPercentChange())));

        detailChangeLabel.getStyleClass().removeAll("gain", "loss", "neutral");
        detailChangeLabel.getStyleClass().add(Formatters.signClass(stock.getPercentChange()));

        // show() decides for itself whether to redraw completely (the selection
        // changed) or just append the newest point (same company, one more tick)
        priceChart.show(stock);
        statisticsPanel.update(stock);

        riskBadge.setText(stock.getRiskLabel() + " risk");
        riskBadge.getStyleClass().removeAll("gain", "loss", "warning", "neutral");
        riskBadge.getStyleClass().add(riskColourClass(stock.getRiskLabel()));
    }

    /** Low risk = green, Very High = red */
    private String riskColourClass(String riskLabel) {
        return switch (riskLabel) {
            case "Low" -> "gain";
            case "Medium" -> "neutral";
            case "High" -> "warning";
            default -> "loss";
        };
    }

    private void onGameFinished() {
        playPauseButton.setDisable(true);
        speedButton.setDisable(true);
        refresh();
    }

    private void stopClock() {
        if (clock != null) {
            clock.stop();
        }
    }

/*
        button handlers
 */
    @FXML
    private void onPlayPause() {
        clock.toggle();
        playPauseButton.setText(clock.getPlayPauseLabel());
        refresh();
    }

    @FXML
    private void onSpeed() {
        ClockSpeed speed = clock.cycleSpeed();
        speedButton.setText(speed.getLabel());
        context.setClockSpeed(speed.getMultiplier());
    }

    @FXML
    private void onPortfolio() {
    }

    @FXML
    private void onSave() {
    }

    @FXML
    private void onMenu() {
        stopClock();
        context.show(ScreenId.MAIN_MENU);
    }
}
