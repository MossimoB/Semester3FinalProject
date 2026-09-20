package org.mossimo.finalprojectsem3final.controller;

import org.mossimo.finalprojectsem3final.app.AppContext;
import org.mossimo.finalprojectsem3final.app.SceneManager;
import org.mossimo.finalprojectsem3final.app.ScreenId;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/*
I will also overdo the comments in this file
 */
public class DashboardController implements SceneManager.ScreenController {

/*
        FXML fields
Each name must match an fx:id in dashboard.fxml exactly, including case
 */
    @FXML private Label difficultyLabel;
    @FXML private Label clockLabel;
    @FXML private Label stateLabel;
    @FXML private ProgressBar progressBar;

    @FXML private Button playPauseButton;
    @FXML private Button speedButton;
    @FXML private Button menuButton;

    @FXML private Label placeholderLabel;

    private AppContext context;

    /**
     * Runs while the FXML loads
     * The context does not exist yet
     * so nothing here may touch it
     */
    @FXML
    public void initialize(){
        // no clock to control yet
        playPauseButton.setDisable(true);
        speedButton.setDisable(true);
    }

    /**
     * Runs after the FXML has landed
     * The context exists from here on
     */
    @Override
    public void init(AppContext context){
        this.context = context;
        difficultyLabel.setText(context.getSelectedDifficulty().getDisplayName());
    }

/*
        button handlers
 */
    @FXML
    public void onPlayPause(){

    }

    @FXML
    public void onSpeed(){

    }

    @FXML
    public void onMenu(){
        context.show(ScreenId.MAIN_MENU);
    }
}
