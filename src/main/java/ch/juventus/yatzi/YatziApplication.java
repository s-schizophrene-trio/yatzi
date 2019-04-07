package ch.juventus.yatzi;

import ch.juventus.yatzi.user.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class YatziApplication extends Application {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final String FXML_MAIN = "view/main.fxml";
    private final String STYLE_MAIN = "css/style.css";

    @Override
    public void start(Stage stage) throws Exception {

        long startTime = System.currentTimeMillis();

        LOGGER.debug("Starting Application");

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Load URLs to resource files for Java FX
        URL fxmlMain = classloader.getResource(FXML_MAIN);
        URL styleMain = classloader.getResource(STYLE_MAIN);

        if (fxmlMain != null && styleMain != null) {

            // Load the main fxml file and set it as parent
            Parent root = FXMLLoader.load(fxmlMain);

            LOGGER.debug("Build Scene");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(styleMain.toExternalForm());

            stage.setTitle("Yatzi Application");
            stage.setScene(scene);
            stage.show();

            // Add Some Mock Data
            User u = new User("Spieler 1");
            LOGGER.debug("Sample User looks like {}", u.toString());

        } else {
            LOGGER.error("Failed to start the application. Make sure you have a {} and {} in your resource folder.", FXML_MAIN, STYLE_MAIN);
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        LOGGER.debug("Application started in {}ms", elapsedTime);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
