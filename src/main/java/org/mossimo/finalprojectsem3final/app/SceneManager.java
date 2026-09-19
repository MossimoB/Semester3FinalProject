package org.mossimo.finalprojectsem3final.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Owns the window and moves between screens
 */
public class SceneManager {
    /**
     * Stylesheets, applied to the Scene once and shared by every screen
     */
    private static final String[] STYLESHEETS = {
            "/org/mossimo/finalprojectsem3final/css/base.css",
            "/org/mossimo/finalprojectsem3final/css/components.css",
            "/org/mossimo/finalprojectsem3final/css/charts.css"
    };

    private final Stage stage;
    private final AppContext context;
    private Scene scene;

    /**
     * The screen currently on display
     */
    private ScreenId currentScreen;

    /**
     * The screen shown before this one
     *
     * The high scores screen is reachable from both the main menu and the
     * results screen, so a hard-coded Back destination strands whoever came the
     * other way. Remembering one step of history fixes it without building a
     * full navigation stack
     */
    private ScreenId previousScreen;

    public SceneManager(Stage stage, AppContext context) {
        this.stage = stage;
        this.context = context;
        context.setSceneManager(this);
    }

    /*
            navigation
     */

    public void show(ScreenId screen) {
        try {
            Parent root = load(screen);

            if (scene == null) {
                // First screen: build the Scene and attach the stylesheets
                scene = new Scene(root, 1180, 760);
                attachStylesheets();
                stage.setScene(scene);
            } else {
                // Later screens: just swap the root.
                // The Scene and its stylesheets stay exactly as they are
                scene.setRoot(root);
            }

            stage.setTitle(screen.getWindowTitle());

            // Only record it as "previous" if we actually moved
            // Showing the same screen twice must not make Back a no-op loop
            if (currentScreen != null && currentScreen != screen) {
                previousScreen = currentScreen;
            }
            currentScreen = screen;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load screen " + screen + " from " + screen.getResourcePath()
                            + ". Check that the file exists under src/main/resources and that the "
                            + "fx:controller attribute names a real class.", e);
        }
    }

}
