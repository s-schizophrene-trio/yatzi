package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.network.Client;
import ch.juventus.yatzi.network.Server;
import ch.juventus.yatzi.network.helper.NetworkUtils;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.ServeType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Setup Controller manages the configuration and mode of the Game. The user is able to define this configs.
 */
public class SetupController implements ViewController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private ViewContext context;

    @FXML
    private Label localIpLabel;

    private String localIpAddress;

    @FXML
    private ProgressIndicator networkProgress;

    @FXML
    private Button startServerButton;

    private ScreenHelper screenHelper;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.localIpAddress = null;
        this.setSetupNotReady();
        this.screenHelper = new ScreenHelper();
    }

    /* ----------------- Initializer --------------------- */

    /**
     * Initialize the Board Controller after the View is rendered.
     * @param context The context of the Main Controller
     */
    @Override
    public void afterInit(ViewContext context) {
        // store the reference to the main context
        this.context = context;

        // define a background
        Image sceneBackgroundImage = this.screenHelper.getImage(this.context.getClassloader(), "background/", "setup_background", "jpg");

        // define background image
        BackgroundImage sceneBackground= new BackgroundImage(
                sceneBackgroundImage,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true));

        // set the correct height for this screen
        AnchorPane anchorPane =  (AnchorPane)context.getRootNode();
        anchorPane.setPrefHeight(400D);
        anchorPane.setBackground(new Background(sceneBackground));

        // initialize network infos
        this.initServerInfo();
    }

    /**
     * Initializes the Network Information, needed to start a Yatzi server
     */
    public void initServerInfo() {
        NetworkUtils networkUtils = new NetworkUtils();
        this.context.getViewHandler().getStatusController().updateStatus("loading network info", true);

        Runnable serverTask = () -> {
            try {
                // set local ip to the create server config
                this.localIpAddress = networkUtils.getLocalIP();

                Platform.runLater(() -> {
                    localIpLabel.setText(localIpAddress);
                    setSetupIsReady();

                    // update the statusbar
                    this.context.getViewHandler().getStatusController().updateStatus("ready", StatusType.OK);
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
        Client client = new Client();
        //client.startConnection();
        this.context.getBoard().setClient(client);
        this.context.getBoard().setServeType(ServeType.CLIENT);
        this.screenHelper.showScreen(context, ScreenType.BOARD, this.context.getStage());
    }

    public void startServer() {

        Server server = new Server();
        server.start(6000);
        this.context.getBoard().setServer(server);
        this.context.getBoard().setServeType(ServeType.SERVER);
        this.screenHelper.showScreen(context, ScreenType.BOARD, this.context.getStage());

    }

}
