package org.mossimo.finalprojectsem3final.controller;

import org.mossimo.finalprojectsem3final.app.AppContext;
import org.mossimo.finalprojectsem3final.app.SceneManager;
import org.mossimo.finalprojectsem3final.app.ScreenId;
import org.mossimo.finalprojectsem3final.model.Market;
import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.ui.MarketTableFactory;
import org.mossimo.finalprojectsem3final.util.Formatters;
import org.mossimo.finalprojectsem3final.util.PriceGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

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
    @FXML private Button menuButton;

    @FXML private TableView<Stock> marketTable;
    @FXML private Label marketIndexLabel;

    private AppContext context;

    /**
     * The market being shown
     */
    private Market market;

    /**
     * The rows in the table
     */
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
    public void init(AppContext context){
        this.context = context;
        difficultyLabel.setText(context.getSelectedDifficulty().getDisplayName());

        // A fresh market at its opening prices
        // Nothing moves yet: the clock is later and until then every Change column reads +0.00%
        market = Market.createDefaultMarket(
                new PriceGenerator(),
                context.getSelectedDifficulty().getVolatilityMultiplier());

        marketRows.setAll(market.getStocks());
        marketTable.getSelectionModel().selectFirst();

        refresh();
    }

    /**
     * Repaints everything from the current model state
     *
     * One method rather than a dozen scattered updates
     */
    private void refresh() {
        marketIndexLabel.setText("Market index "
                + Formatters.percent(market.getMarketIndex()));

        // The Stock objects have changed, but the LIST has not, so TableView has
        // no idea anything happened. refresh() forces every visible cell to
        // re-read its value
        marketTable.refresh();
    }

/*
        button handlers
 */
    @FXML
    public void onPlayPause(){

    }

    @FXML
    public void onSpeed(){

    }

    @FXML
    public void onMenu(){
        context.show(ScreenId.MAIN_MENU);
    }
}
