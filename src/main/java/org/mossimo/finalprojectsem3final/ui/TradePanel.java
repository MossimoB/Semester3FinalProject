package org.mossimo.finalprojectsem3final.ui;

import org.mossimo.finalprojectsem3final.model.Portfolio;
import org.mossimo.finalprojectsem3final.model.Stock;
import org.mossimo.finalprojectsem3final.model.TradeResult;
import org.mossimo.finalprojectsem3final.util.Formatters;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.IntConsumer;

/**
 * Trading controls for the selected company
 */
public class TradePanel extends VBox {

    private static final int MAX_QUANTITY = 1_000_000;

    private final Spinner<Integer> quantitySpinner = new Spinner<>();
    private final Label estimateLabel = new Label();
    private final Label ownedLabel = new Label();
    private final Label messageLabel = new Label();

    private final Button maxButton = new Button("Max");
    private final Button buyButton = new Button("BUY");
    private final Button sellButton = new Button("SELL");

    /** Called with the quantity when Buy is pressed
     * Set by the dashboard
     */
    private IntConsumer onBuy = quantity -> { };

    /** Called with the quantity when Sell is pressed. */
    private IntConsumer onSell = quantity -> { };

    /** The company currently shown so Max can price it */
    private Stock stock;
    private Portfolio portfolio;

    /** No-arg constructor */
    public TradePanel() {
        setSpacing(6);
        getStyleClass().add("trade-row");

        buildSpinner();

        buyButton.getStyleClass().add("button-buy");
        sellButton.getStyleClass().add("button-sell");
        buyButton.setPrefWidth(92);
        sellButton.setPrefWidth(92);
        maxButton.getStyleClass().add("button-ghost");

        estimateLabel.getStyleClass().add("trade-estimate");
        ownedLabel.getStyleClass().addAll("muted-text", "mono");
        messageLabel.getStyleClass().add("trade-message");
        messageLabel.setWrapText(true);

        Label quantityCaption = new Label("Quantity");
        quantityCaption.getStyleClass().add("muted-text");

        // FlowPane, not HBox because HBox squeezes its children when there is not
        // enough width, which truncated "Quantity" to "Qua..." and the estimate
        // to "= $1,4..." on a narrow panel
        // A FlowPane wraps onto a second line instead, so nothing is ever unreadable
        FlowPane controls = new FlowPane(10, 8,
                quantityCaption, quantitySpinner, maxButton,
                estimateLabel, buyButton, sellButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(4, 0, 0, 0));

        getChildren().addAll(ownedLabel, controls, messageLabel);

        wireActions();
    }

    /*
    SETUP
     */
    private void buildSpinner() {
        // A Spinner with no value factory throws a NullPointerException the
        // first time getValue() is called. Setting one is not optional.
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, MAX_QUANTITY, 10));

        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(104);

        // An editable Spinner does NOT commit typed text until the user presses
        // Enter or tabs away. A player who types 50 and clicks Buy would buy the
        // previous value. Committing on every keystroke fixes it.
        quantitySpinner.getEditor().textProperty().addListener((observable, oldText, newText) -> {
            if (newText == null || newText.isBlank()) {
                return;
            }
            try {
                int typed = Integer.parseInt(newText.trim());
                if (typed >= 1 && typed <= MAX_QUANTITY) {
                    quantitySpinner.getValueFactory().setValue(typed);
                }
            } catch (NumberFormatException ignored) {
                // Letters typed into the box. Leave the last good value alone
                // rather than throwing; the editor will show the bad text until
                // they fix it.
            }
        });


}
