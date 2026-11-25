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

    @FXML
    private Label clockLabel;
    @FXML
    private SummaryBar summaryBar;

    @FXML
    private Label positionCountLabel;
    @FXML
    private TableView<HoldingRow> holdingsTable;
    @FXML
    private FlowPane totalsPane;

    @FXML
    private Label tradeCountLabel;
    @FXML
    private TableView<TransactionRow> historyTable;

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

    private void buildHistoryColumns() {
        historyTable.getColumns().clear();

        historyTable.getColumns().add(textColumn("When", 110, TransactionRow::getWhen, true));

        // BUY green, SELL red, matching the buttons that made them.
        TableColumn<TransactionRow, String> type = new TableColumn<>("Type");
        type.setPrefWidth(70);
        type.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        type.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                getStyleClass().removeAll("gain", "loss");
                if (empty || text == null) {
                    setText(null);
                    return;
                }
                setText(text);
                getStyleClass().add("BUY".equals(text) ? "gain" : "loss");
            }
        });
        historyTable.getColumns().add(type);

        historyTable.getColumns().add(textColumn("Symbol", 76, TransactionRow::getSymbol, true));
        historyTable.getColumns().add(textColumn("Company", 130, TransactionRow::getCompanyName, false));

        TableColumn<TransactionRow, String> shares = textColumn("Shares", 80,
                TransactionRow::getShares, true);
        shares.setStyle(RIGHT);
        historyTable.getColumns().add(shares);

        TableColumn<TransactionRow, String> price = textColumn("Price", 100,
                TransactionRow::getPricePerShare, true);
        price.setStyle(RIGHT);
        historyTable.getColumns().add(price);

        TableColumn<TransactionRow, String> total = textColumn("Total", 110,
                TransactionRow::getTotal, true);
        total.setStyle(RIGHT);
        historyTable.getColumns().add(total);

        // Realised profit, coloured by the row's own style class so a BUY reads
        // as a neutral em dash rather than a green zero.
        TableColumn<TransactionRow, String> realised = new TableColumn<>("Realised");
        realised.setPrefWidth(110);
        realised.setStyle(RIGHT);
        realised.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRealised()));
        realised.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                getStyleClass().removeAll("gain", "loss", "neutral");
                if (empty || text == null || getTableRow() == null
                        || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }
                setText(text);
                getStyleClass().addAll("mono", getTableRow().getItem().getRealisedStyleClass());
            }
        });
        historyTable.getColumns().add(realised);

        historyTable.getColumns().forEach(column -> column.setSortable(false));
    }

    /*
    COLUMN HELPERS
     */
    private <T> TableColumn<T, String> textColumn(String title, int width,
                                                  java.util.function.Function<T, String> getter,
                                                  boolean monospaced) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(cell -> new SimpleStringProperty(getter.apply(cell.getValue())));

        if (monospaced) {
            column.setCellFactory(unused -> {
                TableCell<T, String> cell = new TableCell<>() {
                    @Override
                    protected void updateItem(String text, boolean empty) {
                        super.updateItem(text, empty);
                        setText(empty ? null : text);
                    }
                };
                cell.getStyleClass().add("mono");
                return cell;
            });
        }
        return column;
    }

    private <T> TableColumn<T, Double> moneyColumn(String title, int width,
                                                   java.util.function.Function<T, Double> getter,
                                                   boolean coloured) {
        TableColumn<T, Double> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setStyle(RIGHT);
        column.setCellValueFactory(cell -> new SimpleObjectProperty<>(getter.apply(cell.getValue())));
        column.setCellFactory(unused -> new MoneyTableCell<>(coloured));
        return column;
    }

    private <T> TableColumn<T, Double> percentColumn(String title, int width,
                                                     java.util.function.Function<T, Double> getter) {
        TableColumn<T, Double> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setStyle(RIGHT);
        column.setCellValueFactory(cell -> new SimpleObjectProperty<>(getter.apply(cell.getValue())));
        column.setCellFactory(unused -> new PercentTableCell<>(false));
        return column;
    }

    /*
    DATA
     */

    /**
     * Rebuilds both tables and the totals strip from the current portfolio.
     */
    private void loadRows() {
        Portfolio portfolio = game.getPortfolio();
        double totalValue = portfolio.getTotalValue();

        // --- Open positions ---
        List<HoldingRow> positions = new ArrayList<>();
        for (Holding holding : portfolio.getHoldings()) {
            positions.add(new HoldingRow(holding, totalValue));
        }
        // Biggest position first. The player wants to see their largest bet at
        // the top, not whichever company they happened to buy first.
        positions.sort(Comparator.comparingDouble(HoldingRow::getCurrentValue).reversed());
        holdingRows.setAll(positions);

        positionCountLabel.setText(positions.isEmpty()
                ? ""
                : positions.size() + (positions.size() == 1 ? " company" : " companies"));

        // --- Trade history, newest first ---
        List<TransactionRow> history = new ArrayList<>();
        for (Transaction transaction : portfolio.getTransactions()) {
            history.add(new TransactionRow(transaction));
        }
        java.util.Collections.reverse(history);
        historyRows.setAll(history);

        tradeCountLabel.setText(history.isEmpty()
                ? ""
                : history.size() + (history.size() == 1 ? " trade" : " trades"));

        buildTotals(portfolio);
    }

    /**
     * The strip of figures under the holdings table.
     */
    private void buildTotals(Portfolio portfolio) {
        totalsPane.getChildren().clear();

        addTotal("Total invested", Formatters.money(portfolio.getTotalInvested()), null);
        addTotal("Current value", Formatters.money(portfolio.getHoldingsValue()), null);

        double unrealised = portfolio.getUnrealisedProfit();
        addTotal("Unrealised P / L", Formatters.signedMoney(unrealised),
                Formatters.signClass(unrealised));

        double realised = portfolio.getRealisedProfit();
        addTotal("Realised P / L", Formatters.signedMoney(realised),
                Formatters.signClass(realised));

        addTotal("Cash", Formatters.money(portfolio.getCash()), null);
        addTotal("Invested", Formatters.plainPercent(portfolio.getInvestedPercent()), null);
    }

    private void addTotal(String caption, String value, String styleClass) {
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("stat-label");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().addAll("stat-value", "mono");
        if (styleClass != null) {
            valueLabel.getStyleClass().add(styleClass);
        }

        totalsPane.getChildren().add(new VBox(1, captionLabel, valueLabel));
    }

    /*
    NAVIGATION
     */

    @FXML
    private void onBack() {
        context.show(ScreenId.DASHBOARD);
    }

    @FXML
    private void onMenu() {
        context.show(ScreenId.MAIN_MENU);
    }
}