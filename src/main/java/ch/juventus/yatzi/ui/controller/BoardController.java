package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.engine.YatziGame;
import ch.juventus.yatzi.engine.dice.DiceType;
import ch.juventus.yatzi.engine.field.Field;
import ch.juventus.yatzi.engine.field.FieldType;
import ch.juventus.yatzi.engine.field.FieldTypeHelper;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import ch.juventus.yatzi.ui.models.BoardTableRow;
import ch.juventus.yatzi.engine.user.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ch.juventus.yatzi.network.helper.Commands.*;

/**
 * The Board Controller manages the primary Game UI.
 * @author Jan Minder
 */
public class BoardController implements ViewController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final String VIEW_TITLE = "Yatzi Play Board";

    private ViewContext context;
    private ScreenHelper screenHelper;

    private Boolean shouldListen = true;

    @FXML
    private Label screenTitle;

    @FXML
    private TableView<BoardTableRow> tblBoardMain;

    @FXML
    private Label currentUser;

    @FXML
    private Label currentUserId;

    @FXML
    private TableView<User> tblPlayers;

    // list to hold all combinations of the board table
    private List<BoardTableRow> boardTableRows;

    private ExecutorService messageHandlerPool;

    private MessageHandler messageHandler;

    /* ----------------- Initializer --------------------- */

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        screenTitle.setText(VIEW_TITLE);
        screenHelper = new ScreenHelper();

        BasicThreadFactory messagePoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Board Message Handler #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        messageHandlerPool = Executors.newSingleThreadExecutor( messagePoolFactory);
    }

    /**
     * Initialize the Board Controller after the View is rendered.
     *
     * @param context The function of the Main Controller
     */
    @Override
    public void afterInit(ViewContext context) {

        // store the reference to the view function
        this.context = context;

        // listen to the client-message handler queue
        this.messageHandler = this.context.getYatziGame().getClient().getMessageHandler();
        listenToLocalClient(messageHandler);

        // inform the server, this client is ready to get board data
        sendMessageToServer(
                new Transfer(context.getYatziGame().getUserMe().getUserId(), CLIENT_READY)
        );

        // set the correct height for this screen
        AnchorPane anchorPane =  (AnchorPane)context.getRootNode();
        anchorPane.setPrefHeight(850D);

        tblPlayers.setDisable(true);
        tblBoardMain.setDisable(true);
        context.getViewHandler().getStatusController().updateStatus("waiting for data..", true);

        // render the ui
        renderPlayerTable();

        // initialize the ui components
        loadUserStats();
        generatePlayerTable();
        generateBoardTable();

        // define a background
        Image sceneBackgroundImage = screenHelper.getImage(this.context.getClassloader(), "background/", "board_background", "jpg");

        // define background image
        BackgroundImage sceneBackground= new BackgroundImage(
                sceneBackgroundImage,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, new BackgroundPosition(Side.RIGHT, 0,true, Side.TOP, 0, false ),
                new BackgroundSize(1400, 900, false, false, false, false));

        anchorPane.setBackground(new Background(sceneBackground));


        // update status bar
        this.context.getViewHandler().getStatusController().updateServeMode(context.getYatziGame().getServeType());

        // send demo message to server
        //this.function.getBoard().getClient().sendAsyncMessage("Board started message");
    }

    /* ----------------- UI Rendering --------------------- */

    /**
     * Renders the Player Table
     */
    public void renderPlayerTable() {
        TableColumn<User, String> userId = new TableColumn("ID");
        userId.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getShortUserId()
        ));
        tblPlayers.getColumns().add(userId);

        TableColumn userName = new TableColumn("User");
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        tblPlayers.getColumns().add(userName);

        TableColumn<User, ImageView> serveType = new TableColumn("Type");
        // Visualize the Serve Type as Images
        serveType.setCellValueFactory(c -> new SimpleObjectProperty<>(
                renderIconImageView(c.getValue().getServeType().toString(), "png")
        ));
        serveType.setStyle("-fx-alignment: CENTER;");
        tblPlayers.getColumns().add(serveType);
    }

    /**
     * Renders the Image View based on the Image key and file extension (icon)
     *
     * @param imageKey The unique image name without file extension
     * @param fileExt  The file extension of the image
     * @return An Image View based on the Serve Type with an image loaded and resized it.
     */
    private ImageView renderIconImageView(String imageKey, String fileExt) {
        return screenHelper.renderImageView(context.getClassloader(), "icons/", imageKey, fileExt, 20D, 20D);
    }

    /**
     * Loads the user data from the game and add the items to the table.
     */
    public void generatePlayerTable() {
        if (context.getYatziGame() != null) {

            LOGGER.debug("{} users are active", context.getYatziGame().getPlayers().size());

            List<User> users = context.getYatziGame().getPlayers();

            // reset the table first
            tblPlayers.getItems().clear();

            for(User u : users) {
                tblPlayers.getItems().add(u);
            }

        } else {
            LOGGER.error("The reference to the main controller could not be accessed by the board controller");
        }
        LOGGER.debug("users loaded");
    }

    /**
     * Loads all users statistics about the current user
     */
    private void loadUserStats() {
        // Set Current User
        currentUser.setText(context.getYatziGame().getUserMe().getUserName());
        // Set Current User ID
        String userId = context.getYatziGame().getUserMe().getShortUserId();
        currentUserId.setText("ID  " + userId);
    }

    /**
     * Generates the game board table with the user and their score overview.
     */
    public void generateBoardTable() {

        LOGGER.debug("initialize board table");

        tblBoardMain.getColumns().clear();
        tblBoardMain.getItems().clear();

        boardTableRows = new ArrayList<>();
        for (FieldType fieldType : FieldType.values()) {
            boardTableRows.add(new BoardTableRow(new Field(fieldType), context.getYatziGame().getPlayers()));
        }

        // static combinations
        TableColumn<BoardTableRow, VBox> fieldsContainer = new TableColumn("Fields");

        fieldsContainer.setCellValueFactory(c -> new SimpleObjectProperty<>(
                new VBox(new Label(c.getValue().getDescField().getFieldType().toString().toLowerCase()),
                        getFieldTypeImageGroup(c.getValue().getDescField().getFieldType())
                        )
        ));

        fieldsContainer.setSortable(false);
        tblBoardMain.getColumns().add(fieldsContainer);

        // add a user wrapper column
        TableColumn userContainer = new TableColumn("Players");
        userContainer.setSortable(false);
        List<User> users = context.getYatziGame().getPlayers();

        for (int i = 0; i < users.size(); i++) {
            // static combinations
            int index = i;
            TableColumn<BoardTableRow, String> userColumn = new TableColumn(users.get(index).getUserName());
            userColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsers().get(index).getShortUserId()));
            userColumn.setSortable(false);

            User userToLoad = users.get(index);
            UUID activeUserOnBoard = context.getYatziGame().getActiveUserId();
            LOGGER.debug("active user on baord is: {}", activeUserOnBoard);

            if (userToLoad.getUserId().equals(activeUserOnBoard)) {
               userColumn.setStyle("-fx-background-color: rgba(235,242,216,0.7);");
            } else {
                userColumn.setStyle("-fx-background-color: rgba(229,229,229,0.7);");
            }
            userColumn.setEditable(false);
            userContainer.getColumns().add(userColumn);
        }

        // User column to main table
        tblBoardMain.getColumns().add(userContainer);

        // fill the table with the board table items
        tblBoardMain.getItems().addAll(boardTableRows);
    }

    /**
     * Gets an HBox with ImageViews inside of a dice combination
     * @param fieldType The FieldType of the field
     * @return A HBox JavaFx Node
     */
    public HBox getFieldTypeImageGroup(FieldType fieldType) {

        FieldTypeHelper fieldTypeHelper = new FieldTypeHelper();

        // render the hbox
        HBox imageGroup = new HBox();
        imageGroup.setSpacing(4);

        // get dice combination
        List<DiceType> combination = fieldTypeHelper.getDiceCombination(fieldType);

        // loop through all dice types in the combination, and get the according image
        for (DiceType diceType:combination) {
            // get the image for the type
            ImageView diceImageView = screenHelper.renderImageView(context.getClassloader(), "dice/",
                    "dice_"+diceType.toString().toLowerCase(), "png", 15D, 15D);
            imageGroup.getChildren().add(diceImageView);
        }

        return imageGroup;
    }

    private void sendMessageToServer(Transfer transfer){
            context.getYatziGame().getClient().send(transfer);
    }

    /**
     * Listens to the Input Message Queue from the Server
     * @param messageHandler The handler, which should be used to transfer the messages
     */
    public void listenToLocalClient(MessageHandler messageHandler) {

        Runnable messageListener = () -> {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            while (shouldListen) {
                try {
                    if (!messageHandler.getQueue().isEmpty()) {

                        Transfer transfer = messageHandler.getQueue().poll();
                        LOGGER.debug("board-message handler [incoming]: {}", transfer.toString());
                        switch (transfer.getFunction()) {
                            case ROUND_START:
                                YatziGame yatziGame = objectMapper.readValue(transfer.getBody(), YatziGame.class);
                                context.getYatziGame().updateGame(yatziGame);
                                context.getYatziGame().getCircleRoundPlayed().add(yatziGame.getActiveUserId());

                                Platform.runLater(() -> {

                                    generatePlayerTable();
                                    generateBoardTable();
                                    //TODO: Implement round counter
                                    tblPlayers.setDisable(false);
                                    tblBoardMain.setDisable(false);
                                    context.getViewHandler().getStatusController().updateStatus("round 1 started", StatusType.OK);
                                });

                                break;
                            case GAME_CHANGED:
                                YatziGame game = objectMapper.readValue(transfer.getBody(), YatziGame.class);
                                context.getYatziGame().updateGame(game);

                                LOGGER.debug("got new game change from another client. Active user on board: {}", game.getActiveUserId());

                                Platform.runLater(() -> {

                                    generatePlayerTable();
                                    generateBoardTable();

                                    //TODO: Implement round counter
                                    tblPlayers.setDisable(false);
                                    tblBoardMain.setDisable(false);
                                    context.getViewHandler().getStatusController().updateStatus("round 1 in progress", StatusType.OK);
                                });
                                break;
                            case GAME_END:
                                break;
                            case SERVER_EXIT:
                                Platform.runLater(() -> {
                                    showAlert(Alert.AlertType.ERROR, "Server Error", "Server has finished the game.", "Exit the Game?");
                                });
                                break;
                        }
                    }
                } catch (NoSuchElementException e) {
                    LOGGER.error("failed to extract the last element from queue {}", e.getMessage());
                } catch (JsonParseException e) {
                    LOGGER.error("failed to parse the json: {}", e.getMessage());
                } catch (JsonMappingException e) {
                    LOGGER.error("failed to map json values: {}", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
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

    /* ----------------- Actions --------------------- */

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    /**
     * Will be triggered by Server to make a player changed
     */
    @FXML
    private void nextPlayer() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {

            // disable the tables during game update
            tblPlayers.setDisable(true);
            tblBoardMain.setDisable(true);

            String game = objectMapper.writeValueAsString(context.getYatziGame());
            Transfer transfer = new Transfer(this.context.getYatziGame().getUserMe().getUserId(), GAME_CHANGED, game);
            context.getYatziGame().getClient().send(transfer);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void exit(ActionEvent e) {

        switch (context.getYatziGame().getServeType()) {
            case SERVER:
                context.getYatziGame().getClient().stop();
                context.getYatziGame().getServer().stop();
                break;
            case CLIENT:
                context.getYatziGame().getClient().stop();
                break;
        }

        // TODO: Implment clean exit of the board screen
        screenHelper.showScreen(context, ScreenType.SETUP);
    }

}
