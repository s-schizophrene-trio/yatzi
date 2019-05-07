package ch.juventus.yatzi;

import ch.juventus.yatzi.board.Board;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class YatziApplication extends Application {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final String FXML_MAIN = "view/main.fxml";
    private final String STYLE_MAIN = "css/style.css";



    @Override
    public void start(Stage stage) throws Exception {

        long startTime = System.currentTimeMillis();
        Board board;

        LOGGER.debug("starting application");

        // Load the Business
        board = new Board();

        // Load the GUI
        this.startUI(stage, board);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        LOGGER.debug("application started in {}ms", elapsedTime);

    }

    public void startUI(Stage stage, Board board) {
        // Load the UI
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Load URLs to resource files for Java FX
        URL fxmlMain = classloader.getResource(FXML_MAIN);
        URL styleMain = classloader.getResource(STYLE_MAIN);

        // init an empty parent
        Parent root = null;

        if (fxmlMain != null && styleMain != null) {

            try {
                // Load the main fxml file and set it as parent
                root = FXMLLoader.load(fxmlMain);
            } catch (IOException e) {
                LOGGER.error("failed to load the main xml because of {}", e.getMessage());
            }

            LOGGER.debug("build Scene");

            // when the parent is null, the scene will not be loaded and showed.
            if (root != null) {
                Scene scene = new Scene(root);
                scene.getStylesheets().add(styleMain.toExternalForm());

                stage.setTitle("Yatzi Application");
                stage.setScene(scene);
                stage.show();
            } else {
                LOGGER.error("failed to display the scene because of an error during parent initialization");
            }

        } else {
            LOGGER.error("failed to start the application. Make sure you have a {} and {} in your resource folder.", FXML_MAIN, STYLE_MAIN);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
