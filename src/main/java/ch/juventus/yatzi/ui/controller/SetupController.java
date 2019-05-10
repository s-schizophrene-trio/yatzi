package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.helper.ScreenType;
import ch.juventus.yatzi.ui.interfaces.ScreenController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Setup Controller manages the configuration and mode of the Game. The user is able to define this configs.
 * @author Jan Minder
 */
public class SetupController implements ScreenController {

    private MainController mainController;

    @FXML
    private VBox joinServerContainer;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

    }

    /* ----------------- Initializer --------------------- */

    /**
     * Initialize the Board Controller after the View is rendered.
     * @param mainController The context of the Main Controller
     */
    @Override
    public void afterInit(MainController mainController) {
        // store the reference to the main context
        this.mainController = mainController;
    }

    public void showJoinServerInformation() {
        this.joinServerContainer.setVisible(true);
    }

    public void joinServer() {
        this.mainController.showScreen(ScreenType.BOARD);
    }
}
