package ch.juventus.yatzi;

import ch.juventus.yatzi.engine.YatziGame;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import ch.juventus.yatzi.ui.models.FXContext;
import ch.juventus.yatzi.ui.models.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class YatziApplication extends Application {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // JavaFX
    private final String WINDOW_TITLE = "Yatzi Application 1.0";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("starting application");

        this.startUI(stage);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        LOGGER.debug("application started in {}ms", elapsedTime);
    }

    /**Bildschirmfoto 2019-06-04 um 00.48.42
     * Starts all necessary components for the UI, initialize them and start the built UI.
     */
    private void startUI(Stage primaryStage) {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        ScreenHelper screenHelper = new ScreenHelper();
        ViewContext viewContext = new FXContext();

        viewContext.setClassloader(classloader);


        // Create a new Game Instance
        YatziGame yatziGame = this.initYatziGame();

        viewContext.setYatziGame(yatziGame);
        viewContext.setStage(primaryStage);

        // set application icon
        primaryStage.getIcons().add(screenHelper.getImage(classloader, "icons/", "application", "png"));

        // load a view based on the view type
        View view = screenHelper.buildView(classloader,
                screenHelper.getFilePath(ScreenHelper.BASE_PATH_FXML, ScreenType.MAIN, "fxml"),
                ScreenType.MAIN);

        viewContext.setRootNode(view.getNode());
        screenHelper.loadScreenWithController(viewContext, ScreenType.MAIN);

        LOGGER.debug("build the main scene");

        Scene scene = screenHelper.buildScene(viewContext);
        viewContext.setScene(scene);

        if (scene != null) {
            // configure the stage and show the UI
            primaryStage.setTitle(WINDOW_TITLE);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.setResizable(false);
            primaryStage.show();

            // get the controller of the according view and initialize it
            ViewController controller = view.getFxmlLoader().getController();
            controller.afterInit(viewContext);
            screenHelper.centerStageOnScreen(primaryStage);

        } else {
            LOGGER.error("failed to initiate the scenes");
        }
    }

    /**
     * Initialize a new Game Board and set the defaults
     */
    private YatziGame initYatziGame() {
        return new YatziGame();
    }

}
