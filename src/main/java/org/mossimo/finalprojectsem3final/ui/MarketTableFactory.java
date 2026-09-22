package org.mossimo.finalprojectsem3final.ui;

import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.ui.cell.MoneyTableCell;
import org.mossimo.finalprojectsem3final.ui.cell.PercentTableCell;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** Sets up the columns of the market table */
public class MarketTableFactory {

    private MarketTableFactory() {
    }

    /**
     * Applied to numeric columns so the HEADER is right aligned too
     *
     * Setting alignment on the cell only moves the numbers; the header text
     * stays left and sits visibly out of line above them. A TableColumn's own
     * style reaches the header, which is why this goes on the column rather than
     * inside the cell factory
     */
    private static final String RIGHT_ALIGNED_HEADER = "-fx-alignment: CENTER-RIGHT;";

    /**
     * Adds every column to the given table
     *
     * @param table the (empty) TableView from the FXML
     */
    public static void configure(TableView<Stock> table) {

        table.getColumns().clear();

        /*
                symbol
         */
        TableColumn<Stock, String> symbol = new TableColumn<>("Symbol");
        symbol.setPrefWidth(64);

        // cellValueFactory answers "which value goes in this cell?". The lambda
        // receives the whole row and returns the one piece it should show,
        // wrapped in a property because that is what TableView expects.
        symbol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSymbol()));
        symbo].add("mono");
            return cell;
        });

        /*
                company
         */
        TableColumn<Stock, String> company = new TableColumn<>("Company");
        company.setPrefWidth(126);
        company.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCompanyName()));

        /*
                price
         */
        TableColumn<Stock, Double> price = new TableColumn<>("Price");
        price.setPrefWidth(86);
        price.ctProperty<>(cell.getValue().getCurrentPrice()));
        price.setCellFactory(column -> new MoneyTableCell<>(false));

        /*
                change - since the last tick
         */
        TableColumn<Stock, Double> change = new TableColumn<>("Change");
        change.setPrefWidth(92);
        change.setStyle(RIGHT_ALIGNED_HEADER);
        change.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getPercentChange()));
        change.setCellFactory(column -> new PercentTableCell<>(true));


    /*
            overall - since day 1
     */
    TableColumn<Stock, Double> overall = new TableColumn<>("Overall");
        overall.setPrefWidth(84);
        overall.setStyle(RIGHT_ALIGNED_HEADER);
        ov<>(cell.getValue().getTotalPercentChange()));
        overall.setCellFactory(column -> new PercentTableCell<>(false));

        table.getColumns().addAll(symbol, company, price, change, overall);

        // Sorting is switched off deliberately
        table.getColumns().forEach(column -> column.setSortable(false));

        table.setPlaceholder(new javafx.scene.control.Label("No market loaded."));
        table.getStyleClass().add("market-table");
    }
    /**
     * A cell that shows a String and clears itself when reused on an empty row
     *
     * TableView reuses cell objects as you scroll, so a cell that showed a
     * value for row 3 might be handed empty row 40. Clearing it is not optional
     * because if we skip it, then stale text floats in the blank area below the last row
     */
    private static <S> String> plainTextCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                setText(empty ? null : text);
            }
        };
    }
}
