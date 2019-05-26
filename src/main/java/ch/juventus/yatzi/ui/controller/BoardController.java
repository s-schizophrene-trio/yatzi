package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.engine.dice.DiceType;
import ch.juventus.yatzi.engine.field.Field;
import ch.juventus.yatzi.engine.field.FieldType;
import ch.juventus.yatzi.engine.field.FieldTypeHelper;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.helper.Commands;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import ch.juventus.yatzi.ui.models.BoardTableRow;
import ch.juventus.yatzi.engine.user.User;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        messageHandler = new MessageHandler();

        BasicThreadFactory messagePoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Board Message Handler #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        messageHandlerPool = Executors.newSingleThreadExecutor( messagePoolFactory);

        listenToLocalClient(messageHandler);
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

        // set the correct height for this screen
        AnchorPane anchorPane =  (AnchorPane)context.getRootNode();
        anchorPane.setPrefHeight(850D);

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
        this.context.getViewHandler().getStatusController().updateStatus("ready to play", StatusType.OK);

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

            users.forEach(u -> {
                tblPlayers.getItems().add(u);
            });

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

    /**
     * Listens to the Input Message Queue from the Server
     * @param messageHandler The handler, which should be used to transfer the messages
     */
    public void listenToLocalClient(MessageHandler messageHandler) {

        Runnable messageListener = () -> {
            while (shouldListen) {
                try {
                    if (!messageHandler.getQueue().isEmpty()) {
                        Transfer transfer = messageHandler.getQueue().poll();
                        LOGGER.debug("message handler [incoming]: {}", transfer.toString());

                        if (transfer.getFunction().contains(Commands.GAME_READY)) {

                            LOGGER.debug("successfully registered at yatzi server. show ui now.");

                            Platform.runLater(() -> {
                                context.getViewHandler().getStatusController().updateStatus("ready to play", StatusType.OK);
                            });
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

    /* ----------------- Actions --------------------- */

    @FXML
    private void showMessage() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Message Here...");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                LOGGER.debug("pressed OK");
            }
        });
    }

    /**
     * Will be triggered by Server to make a player changed
     */
    private void nextPlayer() {

    }

    @FXML
    public void exit(ActionEvent e) {

        switch (context.getYatziGame().getServeType()) {
            case SERVER:
                context.getYatziGame().getServer().stop();
                break;
            case CLIENT:
                break;
        }

        // TODO: Implment clean exit of the board screen
        screenHelper.showScreen(context, ScreenType.SETUP);
    }

}
