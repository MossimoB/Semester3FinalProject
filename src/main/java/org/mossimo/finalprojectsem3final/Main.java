package org.mossimo.finalprojectsem3final;

import org.mossimo.finalprojectsem3final.app.AppContext;
import org.mossimo.finalprojectsem3final.app.SceneManager;
import org.mossimo.finalprojectsem3final.app.ScreenId;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int MIN_WIDTH = 1000;
    private static final int MIN_HEIGHT = 680;

    /**
     * Called by JavaFX once the toolkit is ready
     *
     * @param stage the window, created and handed over by JavaFX
     */
    @Override
    public void start(Stage stage) {

        // 1. The state that outlives any single screen
        AppContext context = new AppContext();

        // 2. The navigator
        // Its constructor registers itself on the context, so
        // every controller can navigate without holding the Stage directly
        SceneManager sceneManager = new SceneManager(stage, context);

        // 3. A minimum size, so the window cannot be dragged small enough that
        // the layout collapses
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        // 4. Show the first screen
        // This call builds the Scene and attaches the
        // stylesheets, because it is the first one
        sceneManager.show(ScreenId.MAIN_MENU);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
