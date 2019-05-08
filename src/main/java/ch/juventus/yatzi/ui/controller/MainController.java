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
import java.util.HashMap;
import java.util.Map;
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

        // Initaialize the Game
        this.board = this.initBoard();
        this.showScreen(ScreenType.SETUP);

    }

    /** ----------------- Screen Handlers --------------------- */

    public void showScreen(ScreenType screenType) {

        // Load the requested screen from screen loader
        Screen screen = this.loadScreen(screenType);

        try {
            replaceLayout(screen);
            LOGGER.debug("show screen: {}", screenType);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to show screen with type {}", screenType.toString());
        }
    }

    private void replaceLayout(Screen screen) {
        LOGGER.debug("replace layout to new screen: {}", screen.getScreenType());

        // Remove all Nodes from Root Layout
        if (yatziAnchorPane.getChildren().size() > 0) {
            yatziAnchorPane.getChildren().remove(0);
        }

        // Add the loaded Screen Node to the Root Layout
        yatziAnchorPane.getChildren().add(screen.getNode());
    }

    private Screen loadScreen(ScreenType screenType) {

        Screen screen = null;

        switch (screenType) {
            case SETUP:
                // build board screen
                screen = this.buildScreen(FXML_SETUP, screenType);

                // initialize setup screen
                SetupController setupController = screen.getFxmlLoader().getController();
                setupController.setMainController(this);
                break;
            case BOARD:
                // build board screen
                screen = this.buildScreen(FXML_BOARD, screenType);

                // initialize board screen
                BoardController boardController = screen.getFxmlLoader().getController();
                boardController.setMainController(this);
                boardController.loadUsers();
                boardController.loadBoardTable();
                break;
        }

        LOGGER.debug("screen {} loaded", screenType);
        return screen;
    }

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

    /** ----------------- Board Handlers --------------------- */

    private Board initBoard() {
        Board board = new Board();
        board.setIsHost(true);
        return board;
    }

}
