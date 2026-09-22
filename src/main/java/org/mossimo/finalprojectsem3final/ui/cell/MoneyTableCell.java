package org.mossimo.finalprojectsem3final.ui.cell;

import org.mossimo.finalprojectsem3final.util.Formatters;

import javafx.scene.control.TableCell;

/**
 * Displays a {Double} as formatted money
 *
 * Usage in a controller:
 *
 *   priceColumn.setCellValueFactory(cell -&gt;
 *           new SimpleObjectProperty&lt;&gt;(cell.getValue().getCurrentPrice());
 *   priceColumn.setCellFactory(column -&gt; new MoneyTableCell&lt;&gt;(false));
 *
 * @param <S> the row type, e.g. {Stock} or {Holding}
 */
public class MoneyTableCell<S> extends TableCell<S, Double> {

    /**
     * If true, positive values are green, negative red, and a sign is shown
     * Used for profit and loss columns. A plain price column uses false
     */
    private final boolean coloured;

    public MoneyTableCell(boolean coloured) {
        this.coloured = coloured;

        // Numbers belong on the right so the decimal points line up, and in a
        // monospaced font so the digits are all the same width
        getStyleClass().add("mono");
        setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    /** A plain money cell with no colouring */
    public MoneyTableCell() {
        this(false);
    }

    /**
     * Called by JavaFX whenever this cell needs redrawing
     *
     * Two rules that are easy to get wrong:
     *
     *
     *   Always call {super.updateItem} first. It does the
     *       internal bookkeeping and skipping it produces cells that show stale
     *       values after scrolling
     *
     */
    @Override
    protected void updateItem(Double value, boolean empty) {
        super.updateItem(value, empty);

        // Clear any colour left over from the previous row this cell displayed
        getStyleClass().removeAll("gain", "loss", "neutral");

        if (empty || value == null) {
            setText(null);
            return;
        }

        if (coloured) {
            setText(Formatters.signedMoney(value));
            getStyleClass().add(Formatters.signClass(value));
        } else {
            setText(Formatters.money(value));
        }
    }
}
