package org.mossimo.finalprojectsem3final.controller;

import org.mossimo.finalprojectsem3final.app.AppContext;
import org.mossimo.finalprojectsem3final.app.SceneManager;
import org.mossimo.finalprojectsem3final.app.ScreenId;
import org.mossimo.finalprojectsem3final.model.Game; // Justin STILL didn't do it
import org.mossimo.finalprojectsem3final.model.Holding;
import org.mossimo.finalprojectsem3final.model.Portfolio;
import org.mossimo.finalprojectsem3final.model.Transaction;
import org.mossimo.finalprojectsem3final.ui.HoldingRow;  // Justin STILL didn't do it
import org.mossimo.finalprojectsem3final.ui.SummaryBar;
import org.mossimo.finalprojectsem3final.ui.TransactionRow;  // Justin STILL didn't do it
import org.mossimo.finalprojectsem3final.ui.cell.MoneyTableCell;
import org.mossimo.finalprojectsem3final.ui.cell.PercentTableCell;
import org.mossimo.finalprojectsem3final.util.Formatters;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Shows what the player owns and what they have done
 */
public class PortfolioController implements SceneManager.ScreenController {

    private static final String RIGHT = "-fx-alignment: CENTER-RIGHT;";

    @FXML private Label clockLabel;
    @FXML private SummaryBar summaryBar;

    @FXML private Label positionCountLabel;
    @FXML private TableView<HoldingRow> holdingsTable;
    @FXML private FlowPane totalsPane;

    @FXML private Label tradeCountLabel;
    @FXML private TableView<TransactionRow> historyTable;

    private AppContext context;
    private Game game;

    private final ObservableList<HoldingRow> holdingRows = FXCollections.observableArrayList();
    private final ObservableList<TransactionRow> historyRows = FXCollections.observableArrayList();

    /*
    SETUP
     */
    @FXML
    private void initialize() {
        buildHoldingsColumns();
        buildHistoryColumns();

        holdingsTable.setItems(holdingRows);
        historyTable.setItems(historyRows);

        holdingsTable.setPlaceholder(new Label("You do not own any shares yet."));
        historyTable.setPlaceholder(new Label("You have not made any trades yet."));
    }

    @Override
    public void init(AppContext context) {
        this.context = context;
        this.game = context.requireGame();

        clockLabel.setText(game.getSimulation().getClockLabel());
        summaryBar.update(game.getSimulation());

        loadRows();
    }

    /*
    COLUMNS
     */
    private void buildHoldingsColumns() {
        holdingsTable.getColumns().clear();

        holdingsTable.getColumns().add(
                textColumn("Symbol", 76, HoldingRow::getSymbol, true));
        holdingsTable.getColumns().add(
                textColumn("Company", 130, HoldingRow::getCompanyName, false));

        TableColumn<HoldingRow, String> shares = textColumn("Shares", 80,
                row -> Formatters.shares(row.getShares()), true);
        shares.setStyle(RIGHT);
        holdingsTable.getColumns().add(shares);

        holdingsTable.getColumns().add(moneyColumn("Avg cost", 100, HoldingRow::getAverageCost, false));
        holdingsTable.getColumns().add(moneyColumn("Price now", 100, HoldingRow::getCurrentPrice, false));
        holdingsTable.getColumns().add(moneyColumn("Cost basis", 110, HoldingRow::getCostBasis, false));
        holdingsTable.getColumns().add(moneyColumn("Value", 110, HoldingRow::getCurrentValue, false));

        // Coloured: green in profit, red at a loss.
        holdingsTable.getColumns().add(moneyColumn("P / L", 110, HoldingRow::getProfitLoss, true));
        holdingsTable.getColumns().add(percentColumn("Return", 90, HoldingRow::getReturnPercent));

        // How much of the whole account this one position is. A player with 95%
        // in QuantumAI should be able to see that at a glance.
        TableColumn<HoldingRow, String> weight = textColumn("Weight", 80,
                row -> Formatters.plainPercent(row.getWeightPercent()), true);
        weight.setStyle(RIGHT);
        holdingsTable.getColumns().add(weight);

        holdingsTable.getColumns().forEach(column -> column.setSortable(false));
    }

