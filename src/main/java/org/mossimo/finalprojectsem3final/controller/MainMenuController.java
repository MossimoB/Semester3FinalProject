package org.mossimo.finalprojectsem3final.controller;

import org.mossimo.finalprojectsem3final.app.AppContext;
import org.mossimo.finalprojectsem3final.app.SceneManager;
import org.mossimo.finalprojectsem3final.app.ScreenId;
import org.mossimo.finalprojectsem3final.model.Difficulty;
import org.mossimo.finalprojectsem3final.util.Formatters;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.Arrays;

public class MainMenuController implements SceneManager.ScreenController {

    /*
            fxml fields
     */

    @FXML private ComboBox<Difficulty> difficultyCombo;
    @FXML private Label difficultyDescription;
    @FXML private Label difficultyTargets;

    @FXML private Button newGameButton;
    @FXML private Button loadGameButton;
    @FXML private Button highScoresButton;
    @FXML private Button settingsButton;
    @FXML private Button aboutButton;

    private AppContext context;

    /**
     * Called by JavaFX once the FXML has loaded and the fields above are filled
     *
     * The method must be named exactly {initialize} and annotated {@FXML}
     * JavaFX finds it by name, nothing calls it directly
     */
    @FXML
    private void initialize() {

        // Difficulty.values() returns all three constants in declaration order.
        // Adding a fourth difficulty later needs no change here at all.
        difficultyCombo.setItems(FXCollections.observableArrayList(Difficulty.values()));

        // React to the player changing the selection.
        difficultyCombo.valueProperty().addListener(
                (observable, oldValue, newValue) -> showDifficultyDetails(newValue));

        newGameButton.setDisable(false);

        // Everything that needs a screen that does not exist yet is disabled
        // A dead button tells teammates the feature is coming; a missing one
        // makes them think the application is unfinished
        loadGameButton.setDisable(true);
        highScoresButton.setDisable(true);
        settingsButton.setDisable(true);
        aboutButton.setDisable(true);
    }

    /**
     * Runs after the FXML has loaded, once the context exists
     *
     * Setting the value fires the listener registered in
     * {initialize()}, which fills in both labels for free
     */
    @Override
    public void init(AppContext context) {
        this.context = context;
        difficultyCombo.setValue(context.getSelectedDifficulty());
    }

    /**
     * Fills in the two labels under the dropdown
     *
     * Every number here is calculated from the enum. Nothing is typed into
     * the FXML, so changing a target in {@link Difficulty} updates this screen
     * with no further work
     */
    private void showDifficultyDetails(Difficulty difficulty) {
        if (difficulty == null) {
            return;
        }

        difficultyDescription.setText(difficulty.getDescription());

        difficultyTargets.setText(String.format("%s  →  %s     %s in %d days",
                Formatters.wholeMoney(difficulty.getStartingCash()),
                Formatters.wholeMoney(difficulty.getTargetValue()),
                Formatters.percent(difficulty.getRequiredGainPercent()),
                difficulty.getTotalDays()));

        // Remember the choice, so the dashboard and the next visit to this
        // screen both agree with what the player picked
        //
        // The null check matters: this method runs from the listener, which can
        // fire during initialize() before init() has handed over the context
        if (context != null) {
            context.setSelectedDifficulty(difficulty);
        }
    }

    /*
            button handlers
     */

    // This will open the dashboard
    @FXML
    private void onNewGame() {
        context.show(ScreenId.DASHBOARD);
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
