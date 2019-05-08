package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.board.Board;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

@Getter
@Setter
public class MainController implements Initializable {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Board board;

    @FXML
    private AnchorPane yatziAnchorPane;

    // Resource Paths
    private final String FXML_BOARD = "view/board.fxml";
    private final String STYLE_MAIN = "css/style.css";

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        LOGGER.debug("initialize main controller");

        // Initaialize the Game
        this.board  = this.initBoard();

        // Load the Board Controller
        loadBoardScreen(resources);
    }

    private Board initBoard() {
        Board board = new Board();
        board.setIsHost(true);

        return board;
    }

    private void loadBoardScreen(ResourceBundle resources) {

        LOGGER.debug("initialize board screen");

        // Load the UI
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // Load URLs to resource files for Java FX
        URL fxmlBoard = classloader.getResource(FXML_BOARD);

        if (fxmlBoard != null) {
            try {
                // Load the main fxml file and set it as parent
                FXMLLoader boardLoader = new FXMLLoader(fxmlBoard);
                boardLoader.setResources(resources);

                // Add the board layout to the main layout
                yatziAnchorPane.getChildren().add(boardLoader.load());

                // Initialize controllers
                BoardController boardController = boardLoader.getController();
                boardController.setBoard(this.board);

                boardController.loadUsers();
                boardController.loadBoardTable();

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("failed to load the board because of {}", e.getMessage());
            }
        } else {
            LOGGER.error("Could not load the board screen fxml file {}", FXML_BOARD);
        }
        LOGGER.debug("board screen loaded");
    }

    private Locale getSystemLocale() {
        Locale locale = null;
        if (locale == null) {
            locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
        }
        return locale;
    }

}
