package ch.juventus.yatzi;

import ch.juventus.yatzi.ui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
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

    // Stage Holder
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("starting application");

        // hold the stage context
        this.primaryStage = stage;

        // Load the GUI
        this.startUI();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        LOGGER.debug("application started in {}ms", elapsedTime);
    }

    /**
     * Starts all necessary components for the UI, initialize them and start the built UI.
     */
    private void startUI() {
        // Load the UI
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Load URLs to resource files for Java FX
        URL fxmlPath = classloader.getResource(FXML_MAIN);
        URL stylePath = classloader.getResource(STYLE_MAIN);

        AnchorPane layoutRoot = null;

        if (fxmlPath != null && stylePath != null) {

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlPath);

            try {
                // Load the main fxml file and set it as parent
                layoutRoot = fxmlLoader.load();

                // set the global main context to the main controller
                MainController mainController = fxmlLoader.getController();
                mainController.afterInit(this);

            } catch (IOException e) {
                LOGGER.error("failed to load the main xml because of {}", e.getMessage());
            }

            LOGGER.debug("build Scene");

            // build the main scene
            Scene scene = this.buildScene(layoutRoot, stylePath);

            if (scene != null) {
                // configure the stage and show the UI
                this.primaryStage.setTitle(WINDOW_TITLE);
                this.primaryStage.setScene(scene);
                this.primaryStage.sizeToScene();
                this.primaryStage.setResizable(false);
                this.primaryStage.show();

                // initial center stage
                this.centerStage();

            } else {
                LOGGER.error("failed to initiate the scenes");
            }
        } else {
            LOGGER.error("failed to start the application. Make sure you have a {} and {} in your resource folder.", FXML_MAIN, STYLE_MAIN);
        }
    }

    /**
     * Builds a nwe scene based on the AnchorPane. The Global Style File will be added to the stylesheet-chain.
     * @param root The AnchorPane Layout from the Root fxml
     * @param styleUrl An absolute URL to the stylesheet file
     * @return The built scene, ready to show.
     */
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

    /**
     * Gets the Primary Stage (JavaFX Window)
     * @return The Primary Stage
     */
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    /**
     * Centers a Window on the Screen
     */
    public void centerStage() {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        this.primaryStage.setX((primScreenBounds.getWidth() - this.primaryStage.getWidth()) / 2);
        this.primaryStage.setY((primScreenBounds.getHeight() - this.primaryStage.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
