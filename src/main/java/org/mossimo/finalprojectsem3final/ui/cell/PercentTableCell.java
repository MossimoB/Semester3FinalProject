package org.mossimo.finalprojectsem3final.ui.cell;

import org.mossimo.finalprojectsem3final.util.Formatters;

import javafx.scene.control.TableCell;

/**
 * Displays a {Double} as a signed percentage and coloured by sign
 *
 *    2.4  →  "+2.40%"  in green
 *   -1.1  →  "-1.10%"  in red
 *    0.0  →  "+0.00%"  in grey
 *
 * @param <S> the row type
 */
public class PercentTableCell<S> extends TableCell<S, Double> {

    /** If true, an arrow is prefixed. Used on the market table's change column */
    private final boolean showArrow;

    public PercentTableCell(boolean showArrow) {
        this.showArrow = showArrow;
        getStyleClass().add("mono");
        setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    public PercentTableCell() {
        this(false);
    }

    @Override
    protected void updateItem(Double value, boolean empty) {
        super.updateItem(value, empty);

        // Cells are reused as the table scrolls, so any previous colour has to
        // be stripped before a new one is applied. See MoneyTableCell.
        getStyleClass().removeAll("gain", "loss", "neutral");

        if (empty || value == null) {
            setText(null);
            return;
        }

        String text = Formatters.percent(value);

        if (showArrow) {
            // A redundant cue on purpose because colour should never be
            // the ONLY way information is conveyed
            String arrow = value > 0 ? "▲ " : value < 0 ? "▼ " : "  ";
            text = arrow + text;
        }

        setText(text);
        getStyleClass().add(Formatters.signClass(value));
    }
}
