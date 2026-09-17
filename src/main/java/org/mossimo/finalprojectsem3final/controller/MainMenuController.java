package org.mossimo.finalprojectsem3final.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML private ComboBox<String> difficultyCombo;
    @FXML private Label difficultyDescription;
    @FXML private Label difficultyTargets;

    @FXML private Button newGameButton;
    @FXML private Button loadGameButton;
    @FXML private Button highScoresButton;
    @FXML private Button settingsButton;
    @FXML private Button aboutButton;

    /**
     * Called by JavaFX once the FXML has loaded and the fields above are filled
     *
     * The method must be named exactly {@code initialize} and annotated
     * {@code @FXML}. JavaFX finds it by name; nothing calls it directly
     */
    @FXML
    private void initialize() {

        // Placeholder text so the screen does not look broken
        difficultyCombo.setItems(FXCollections.observableArrayList("Easy", "Normal", "Hard"));
        difficultyCombo.setValue("Normal");

        difficultyDescription.setText("The standard game. Reach $15,000 in ten trading days.");
        difficultyTargets.setText("$10,000  →  $15,000     +50.00% in 10 days");

        // Everything that needs a screen that does not exist yet is disabled
        // A dead button tells teammates the feature is coming; a missing one
        // makes them think the application is unfinished
        newGameButton.setDisable(true);
        loadGameButton.setDisable(true);
        highScoresButton.setDisable(true);
        settingsButton.setDisable(true);
        aboutButton.setDisable(true);
    }

    /*
     * Button Handlers
     *
     * Each one matches an onAction="#name" in the FXML. They are empty for now,
     * but they must exist or the FXML will not load.
     */

    // This will open the dashboard
    @FXML
    private void onNewGame() {
    }

    // This will load a saved game
    @FXML
    private void onLoadGame() {
    }

    // high score screen
    @FXML
    private void onHighScores() {
    }

    // settings screen
    @FXML
    private void onSettings() {
    }

    // about screen
    @FXML
    private void onAbout() {
    }

    // quit button
    @FXML
    private void onQuit() {
        Platform.exit();
    }
}
