package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.engine.user.User;
import ch.juventus.yatzi.network.client.Client;
import ch.juventus.yatzi.network.helper.Commands;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.network.server.Server;
import ch.juventus.yatzi.network.helper.NetworkUtils;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.ServeType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ch.juventus.yatzi.network.helper.Commands.*;
import static ch.juventus.yatzi.ui.enums.ServeType.CLIENT;
import static ch.juventus.yatzi.ui.enums.ServeType.SERVER;

/**
 * The Setup Controller manages the configuration and mode of the Game. The user is able to define this configs.
 */
public class SetupController implements ViewController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @FXML
    private Label localIpLabel;
    @FXML
    private String localIpAddress;
    @FXML
    private ProgressIndicator networkProgress;
    @FXML
    private TextField userName;
    @FXML
    private VBox setupUsersContainer;
    @FXML
    private GridPane setupUsersGrid;

    @FXML
    private TextField localServerPort;
    @FXML
    private Button btnStartServer;
    @FXML
    private TextField remoteServerIp;
    @FXML
    private TextField remoteServerPort;
    @FXML
    private Tab tabJoinHost;
    @FXML
    private Tab tabHostServer;
    @FXML
    private Button btnJoinServer;
    @FXML
    private VBox vboxStartServerContainer;
    @FXML
    private VBox vboxStartServerOuterContainer;

    private Button btnStartGame;
    private ViewContext context;
    private ScreenHelper screenHelper;
    private MessageHandler messageHandler;
    private Boolean shouldListen = true;
    private ExecutorService messageHandlerPool;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        localIpAddress = "";
        setSetupNotReady();
        messageHandler = new MessageHandler();
        screenHelper = new ScreenHelper();
        btnStartGame = new Button();
        btnStartGame.getStyleClass().add("button");
        btnStartGame.setText("Start Game");
        btnStartGame.addEventHandler(ActionEvent.ACTION,
                event -> {
                   startGame();
                });

        BasicThreadFactory messagePoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Setup Message Handler #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        messageHandlerPool = Executors.newSingleThreadExecutor(messagePoolFactory);
    }

    /* ----------------- Initializer --------------------- */

    /**
     * Initialize the Board Controller after the View is rendered.
     *
     * @param context The function of the Main Controller
     */
    @Override
    public void afterInit(ViewContext context) {
        // store the reference to the main function
        this.context = context;

        // define a background
        Image sceneBackgroundImage = screenHelper.getImage(this.context.getClassloader(), "background/", "setup_background", "jpg");

        // define background image
        BackgroundImage sceneBackground = new BackgroundImage(
                sceneBackgroundImage,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true));

        // set the correct height for this screen
        AnchorPane anchorPane = (AnchorPane) context.getRootNode();
        anchorPane.setPrefHeight(400D);
        anchorPane.setBackground(new Background(sceneBackground));

        // initialize network infos
        initServerInfo();
    }

    /**
     * Initializes the Network Information, needed to start a Yatzi server
     */
    public void initServerInfo() {
        NetworkUtils networkUtils = new NetworkUtils();
        context.getViewHandler().getStatusController().updateStatus("loading network info", true);

        Runnable serverTask = () -> {
            try {
                // set local ip to the create server config
                localIpAddress = networkUtils.getLocalIP();

                Platform.runLater(() -> {
                    localIpLabel.setText(localIpAddress);
                    setSetupIsReady();

                    // update the statusbar
                    context.getViewHandler().getStatusController().updateStatus("ready", StatusType.OK);
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
        btnStartServer.setDisable(true);
        networkProgress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    /**
     * Enables all UI Components which depends on network Information
     */
    private void setSetupIsReady() {
        btnStartServer.setDisable(false);
        networkProgress.setVisible(false);
    }

    /* ----------------- Server --------------------- */

    public void joinServerAction() {
        if (!userName.getText().isEmpty()) {
            userName.setDisable(true);
            tabHostServer.setDisable(true);
            btnJoinServer.setDisable(true);
            joinServer(CLIENT);
        } else {
            context.getViewHandler().getStatusController().showError("username can not be empty");
        }
    }

    private void joinServer(ServeType serveType) {

        User localUser = new User(userName.getText(), serveType);
        context.getYatziGame().getUserService().registerUser(localUser, true);

        context.getYatziGame().setServeType(serveType);

        context.getViewHandler().getStatusController().updateStatus("connect to server..", true);
        context.getYatziGame().setClient(setupClient(localUser));
        startServerListener(messageHandler);
    }

    /**
     * Verify the user inputs and start a new server instance.
     */
    public void startServerAction() {

        int port = Integer.parseInt(localServerPort.getText());
        context.getViewHandler().getStatusController().updateStatus("setup server..", true);

        Server server = new Server();
        context.getYatziGame().setServer(server);

        try {

            if (port < 1000) {
                context.getViewHandler().getStatusController().showError("port have to be > 1000");
            } else {

                if (!userName.getText().isEmpty()) {

                    joinServer(SERVER);
                    server.start(port, context.getYatziGame());
                    context.getYatziGame().setServer(server);

                    tabJoinHost.setDisable(true);
                    userName.setDisable(true);

                } else {
                    context.getViewHandler().getStatusController().showError("username can not be empty");
                }
            }

        } catch (Exception e) {
            LOGGER.error("failed to start the server");
            e.printStackTrace();
            context.getViewHandler().getStatusController().showError("failed to start the server");
        }
    }

    /**
     * Listens to the Input Message Queue from the Server
     *
     * @param messageHandler The handler, which should be used to transfer the messages
     */
    private void startServerListener(MessageHandler messageHandler) {

        Runnable messageListener = () -> {
            ObjectMapper objectMapper = new ObjectMapper();
            while (shouldListen) {
                try {
                    if (!messageHandler.getQueue().isEmpty()) {
                        Transfer transfer = messageHandler.getQueue().poll();
                        LOGGER.debug("client-message handler [incoming]: {}", transfer.toString());

                        switch (transfer.getFunction()) {
                            case PLAYER_NEW:
                                try {
                                    User user = objectMapper.readValue(transfer.getBody(), User.class);
                                    newPlayerJoined(user);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case WAIT_FOR_GAME_READY:
                                Platform.runLater(() -> {
                                    context.getViewHandler().getStatusController().updateStatus("wait for host to start game", StatusType.PENDING);
                                    btnJoinServer.setDisable(true);
                                    tabHostServer.setDisable(true);
                                });
                                break;
                            case MAX_PLAYERS_REACHED:
                                Platform.runLater(() -> {
                                    context.getViewHandler().getStatusController().showError("max amount of players reached.");
                                });
                                break;
                            case GAME_READY:
                                Platform.runLater(() -> {
                                    // stop message listener
                                    this.shouldListen = false;
                                    context.getViewHandler().getScreenHelper().showScreen(context, ScreenType.BOARD);
                                });
                                break;
                        }
                    }
                } catch (NoSuchElementException e) {
                    LOGGER.error("failed to extract the last element from queue");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread messageListenerTask = new Thread(messageListener);
        messageHandlerPool.submit(messageListenerTask);
    }

    /**
     * A new user has joined the game and will be registered through the main client
     * @param user
     */
    private void newPlayerJoined(User user) {
        Platform.runLater(() -> {
            try {
                setupUsersContainer.setVisible(true);
                context.getViewHandler().getStatusController().updateStatus("waiting for players..", true);


                context.getYatziGame().getUserService().registerUser(user, false);

                Label lblUser = new Label();
                lblUser.getStyleClass().add("text-field-value");
                lblUser.setText(user.getUserName());

                Label lblUserId = new Label();
                lblUserId.getStyleClass().add("text-field-value");
                lblUserId.setText(user.getShortUserId());

                int rowIndex = setupUsersGrid.getRowCount();

                setupUsersGrid.add(lblUser, 0, rowIndex);
                setupUsersGrid.add(lblUserId, 1, rowIndex);

                // enable the start game button
                btnStartServer.setVisible(false);

                // only when more than 1 player is joined
                if (setupUsersGrid.getRowCount() > 1) {
                    vboxStartServerOuterContainer.getChildren().remove(btnStartServer);
                    vboxStartServerContainer.getChildren().add(btnStartGame);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to update the user list: {}", e.getMessage());
            }
        });
    }

    /**
     * Sends a broadcast message to all clients
     */
    public void startGame() {

        User randomUser = context.getYatziGame().getRandomActiveUser();

        context.getYatziGame().setActiveUserId(randomUser.getUserId());
        context.getYatziGame().getServer().broadcastMessage(new Transfer(Commands.GAME_READY), true);
    }

    /**
     * Connects to the server socket (local and remote)
     */
    private Client setupClient(User user) {

            // connects to the server
            Client client = new Client(
                    remoteServerIp.getText(),
                    Integer.parseInt(remoteServerPort.getText()),
                    user.getUserId(),
                    messageHandler
            );

            client.connect(context);

            return client;
    }
}
