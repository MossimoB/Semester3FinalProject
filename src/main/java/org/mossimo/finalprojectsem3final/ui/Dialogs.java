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
     * PUBLIC API
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


}
