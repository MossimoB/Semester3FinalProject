package org.mossimo.finalprojectsem3final;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private static final int WIDTH = 1180;
    private static final int HEIGHT = 760;

    @Override
    public void start(Stage stage) throws Exception {

        // Find the layout on the classpath
        URL layout = getClass().getResource("/org/mossimo/finalprojectsem3final/view/main-menu.fxml");

        if (layout == null) {
            // The most common JavaFX setup mistake gets a real message
            // rather than a bare NullPointerException
            throw new IllegalStateException(
                    "Could not find /org/mossimo/finalprojectsem3final/view/main-menu.fxml on the classpath.\n"
                            + "  Expected file: src/main/resources/org/mossimo/finalprojectsem3final/view/main-menu.fxml\n"
                            + "  If it is there, run 'mvn clean compile' so Maven copies it.");
        }

        Parent root = FXMLLoader.load(layout);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // The stylesheet is attached to the SCENE, so every control inside it
        // inherits the theme
        URL stylesheet = getClass().getResource("org/mossimo/finalprojectsem3final/css/base.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("StockSim");
        stage.setScene(scene);

        stage.setMinWidth(1000);
        stage.setMinHeight(680);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
