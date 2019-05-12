package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.network.helper.NetworkUtils;
import ch.juventus.yatzi.ui.helper.ScreenType;
import ch.juventus.yatzi.ui.helper.ServeType;
import ch.juventus.yatzi.ui.helper.StatusType;
import ch.juventus.yatzi.ui.interfaces.ScreenController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Setup Controller manages the configuration and mode of the Game. The user is able to define this configs.
 * @author Jan Minder
 */
public class SetupController implements ScreenController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private MainController mainController;

    @FXML
    private VBox joinServerContainer;

    @FXML
    private Label localIpLabel;

    private String localIpAddress;

    @FXML
    private ProgressIndicator networkProgress;

    @FXML
    private Button startServerButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.localIpAddress = null;
        this.setSetupNotReady();
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

        // set the correct height for this screen
        this.mainController.getYatziAnchorPane().setPrefHeight(400D);

        // initialize network infos
        this.initServerInfo();
    }

    /**
     * Initializes the Network Information, needed to start a Yatzi server
     */
    public void initServerInfo() {
        NetworkUtils networkUtils = new NetworkUtils();
        this.mainController.getStatusController().updateStatus("loading network info", true);

        Runnable serverTask = () -> {
            try {
                // set local ip to the create server config
                this.localIpAddress = networkUtils.getLocalIP();

                Platform.runLater(() -> {
                    localIpLabel.setText(localIpAddress);
                    setSetupIsReady();
                    this.mainController.getStatusController().updateStatus("ready", StatusType.OK);
                });

            } catch (Exception e) {
                LOGGER.error("unprocessable client request {}", e.getMessage());
                e.printStackTrace();
            }
        };

        // start the server thread
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    /* ----------------- UI Helper ------------------- */

    /**
     * Disables all UI Components which depends on network information
     */
    private void setSetupNotReady() {
        this.startServerButton.setDisable(true);
        this.networkProgress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    /**
     * Enables all UI Components which depends on network Information
     */
    private void setSetupIsReady() {
        startServerButton.setDisable(false);
        networkProgress.setVisible(false);
    }

    /* ----------------- Server --------------------- */

    public void joinServer() {
        this.mainController.showScreen(ScreenType.BOARD);
    }

    public void startServer() {
        this.mainController.getServer().start(6000);
        this.mainController.setSelectedServeType(ServeType.SERVER);
        this.mainController.showScreen(ScreenType.BOARD);
    }
}
