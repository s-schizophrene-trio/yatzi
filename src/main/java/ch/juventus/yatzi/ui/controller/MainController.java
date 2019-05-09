package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.board.Board;
import ch.juventus.yatzi.ui.helper.ScreenType;
import ch.juventus.yatzi.ui.models.Screen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class MainController implements Initializable {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // Resource Paths
    private final String FXML_BOARD = "view/board.fxml";
    private final String FXML_SETUP = "view/setup.fxml";

    // Holds the whole game board
    private Board board;

    @FXML
    private AnchorPane yatziAnchorPane;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        LOGGER.debug("initialize main controller");

        // TODO: The right location to initialization?
        this.initBoard();

        this.showScreen(ScreenType.BOARD);

    }

    /* ----------------- Screen Handlers --------------------- */

    public void showScreen(ScreenType screenType) {

        // Load the requested screen from screen loader
        Screen screen = this.loadScreenWithController(screenType);

        try {
            replaceLayout(screen);
            LOGGER.debug("show screen: {}", screenType);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to show screen with type {}", screenType.toString());
        }
    }

    /**
     * Replaces the current layout with a new JavaFX Node. If the Root View has existing child,
     * they will be removed first.
     * @param screen the screen object with the according layout node in it.
     */
    private void replaceLayout(Screen screen) {
        LOGGER.debug("replace layout to new screen: {}", screen.getScreenType());

        // Remove all Nodes from Root Layout
        if (yatziAnchorPane.getChildren().size() > 0) {
            yatziAnchorPane.getChildren().remove(0);
        }

        // Add the loaded Screen Node to the Root Layout
        yatziAnchorPane.getChildren().add(screen.getNode());
    }

    /**
     * Loads a screen including the according controller based on a screen type. If the
     * screen type is unknown by this function,
     * an error will logged.
     * @param screenType the screen type to load
     * @return an initialized screen object based on the screen type.
     */
    private Screen loadScreenWithController(ScreenType screenType) {

        Screen screen = null;

        switch (screenType) {
            case SETUP:
                // build board screen
                screen = this.buildScreen(FXML_SETUP, screenType);
                // initialize setup screen
                SetupController setupController = screen.getFxmlLoader().getController();
                setupController.afterInit(this);
                break;
            case BOARD:
                // build board screen
                screen = this.buildScreen(FXML_BOARD, screenType);
                // initialize board screen
                BoardController boardController = screen.getFxmlLoader().getController();
                boardController.afterInit(this);
                break;
            default:
                LOGGER.error("The screen type {} could not be handled.", screenType);
                break;
        }

        LOGGER.debug("screen {} loaded", screenType);
        return screen;
    }

    /**
     * Builds a screen object based on the fxml path and the according screen type
     * @param fxmlPath The relative path to the fxml layout file with base src/main/resources/
     * @param screenType The screen type to load
     * @return An initialized Screen object with a loaded fxml file.
     */
    private Screen buildScreen(String fxmlPath, ScreenType screenType) {

        Node node = null;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        URL fxmlUrl = classloader.getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

        LOGGER.debug("build {} screen", screenType);

        try {
            node = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to load screen with type: {}", screenType);
        }

        return new Screen(fxmlLoader,  node, screenType);
    }

    /* ----------------- Board Handlers --------------------- */

    /**
     * Initialize a new Game Board and set the defaults
     * @return
     */
    private void initBoard() {
        this.board = new Board();
    }

}
