package org.mossimo.finalprojectsem3final.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

import java.net.URL;
import java.util.Optional;

/**
 * Standard dialogs for StockSim
 */
public class Dialogs {

    private Dialogs() {
    }

    /*
    PUBLIC API
     *

     */
    /** Tells the player something */
    public static void info(Window owner, String title, String message) {
        show(owner, Alert.AlertType.INFORMATION, title, message);
    }

    /** Warns the player about something*/
    public static void warning(Window owner, String title, String message) {
        show(owner, Alert.AlertType.WARNING, title, message);
    }

    /** Reports a failure */
    public static void error(Window owner, String title, String message) {
        show(owner, Alert.AlertType.ERROR, title, message);
    }

    /**
     * Asks a yes or no question
     *
     * @return true if the player confirmed
     */
    public static boolean confirm(Window owner, String title, String message) {
        Alert alert = build(owner, Alert.AlertType.CONFIRMATION, title, message);

        // The default buttons are OK and Cancel, which are vague. Naming the
        // action ("Abandon game") means the player can answer without rereading
        // the question.
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No", ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(no, yes);

        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == yes;
    }


    /**
     * Asks a yes or no question with custom button text
     *
     * @param confirmText what the yes button says
     */
    public static boolean confirm(Window owner, String title, String message,
                                  String confirmText, String cancelText) {
        Alert alert = build(owner, Alert.AlertType.CONFIRMATION, title, message);

        ButtonType confirm = new ButtonType(confirmText);
        ButtonType cancel = new ButtonType(cancelText, ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(cancel, confirm);

        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == confirm;
    }

    /*
    INTERNALS
     */
    private static void show(Window owner, Alert.AlertType type, String title, String message) {
        build(owner, type, title, message).showAndWait();
    }

    /**
     * Creates an Alert wearing the application's stylesheets
     *
     * A JavaFX Alert opens in its own Window with its own Scene, so it does
     * NOT inherit the stylesheets attached to the main scene
     * Without this method, every dialog in a dark-themed
     * application appears as a bright grey system box
     */
    private static Alert build(Window owner, Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);

        if (owner != null) {
            // Centres the dialog on the app and stops the player clicking behind it.
            alert.initOwner(owner);
        }

        alert.setTitle(title);

        // setHeaderText(null) removes the default empty banner above the message
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane pane = alert.getDialogPane();
        pane.setMinWidth(420);
        applyStylesheets(pane);
        pane.getStyleClass().add("app-dialog");

        return alert;
    }

    /** Attaches the app stylesheets to a dialog's own scene */
    private static void applyStylesheets(DialogPane pane) {
        for (String path : new String[]{
                "/com/stocksim/css/base.css",
                "/com/stocksim/css/components.css"}) {

            URL url = Dialogs.class.getResource(path);
            if (url != null) {
                pane.getStylesheets().add(url.toExternalForm());
            }
        }
    }
}
