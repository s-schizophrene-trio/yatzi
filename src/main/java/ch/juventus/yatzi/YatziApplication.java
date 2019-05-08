package ch.juventus.yatzi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class YatziApplication extends Application {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // JavaFX
    private final String FXML_MAIN = "view/main.fxml";
    private final String STYLE_MAIN = "css/style.css";
    private final String WINDOW_TITLE = "Yatzi Application 1.0";

    @Override
    public void start(Stage stage) {
        long startTime = System.currentTimeMillis();
        LOGGER.debug("starting application");

        // Load the GUI
        this.startUI(stage);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        LOGGER.debug("application started in {}ms", elapsedTime);
    }

    private void startUI(Stage stage) {
        // Load the UI
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Load URLs to resource files for Java FX
        URL fxmlPath = classloader.getResource(FXML_MAIN);
        URL stylePath = classloader.getResource(STYLE_MAIN);

        AnchorPane layoutRoot = null;

        if (fxmlPath != null && stylePath != null) {

            try {
                // Load the main fxml file and set it as parent
                layoutRoot = FXMLLoader.load(fxmlPath);
            } catch (IOException e) {
                LOGGER.error("failed to load the main xml because of {}", e.getMessage());
            }

            LOGGER.debug("build Scene");

            // build the main scene
            Scene scene = this.buildScene(layoutRoot, stylePath);

            if (scene != null) {
                // configure the stage and show the UI
                stage.setTitle(WINDOW_TITLE);
                stage.setScene(scene);
                stage.show();
            } else {
                LOGGER.error("failed to initiate the scenes");
            }
        } else {
            LOGGER.error("failed to start the application. Make sure you have a {} and {} in your resource folder.", FXML_MAIN, STYLE_MAIN);
        }
    }

    private Scene buildScene(AnchorPane root, URL styleUrl) {
        if (root != null) {
            Scene scene = new Scene(root);
            scene.getStylesheets().add(styleUrl.toExternalForm());
            return scene;
        } else {
            LOGGER.error("failed to display the scene because of an error during parent initialization");
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
