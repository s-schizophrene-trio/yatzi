package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.config.ApplicationConfig;
import ch.juventus.yatzi.game.YatziGame;
import ch.juventus.yatzi.game.board.score.ScoreService;
import ch.juventus.yatzi.game.dice.Dice;
import ch.juventus.yatzi.game.dice.DiceType;
import ch.juventus.yatzi.game.field.Field;
import ch.juventus.yatzi.game.field.FieldType;
import ch.juventus.yatzi.game.field.FieldTypeHelper;
import ch.juventus.yatzi.game.logic.BoardManager;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.ui.enums.ActionType;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.helper.UserRow;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import ch.juventus.yatzi.ui.helper.ActionCell;
import ch.juventus.yatzi.ui.models.ActionField;
import ch.juventus.yatzi.ui.models.BoardTableRow;
import ch.juventus.yatzi.game.user.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import lombok.Getter;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static ch.juventus.yatzi.network.helper.Commands.*;

/**
 * The Board Controller manages the primary Game UI.
 */
public class BoardController implements ViewController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final String IMAGE_BUTTON_CLASS = "image-button";

    @Getter
    private ViewContext context;
    private BoardController self;
    private ScreenHelper screenHelper;
    private ApplicationConfig config;

    private Boolean shouldListen = true;
    private ObservableList<BoardTableRow> boardTableRows;

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

    @FXML
    private HBox diceContainer;

    @FXML
    private Button btnRollTheDice;

    @FXML
    private Label diceAttempts;

    private Lighting lightingGray;
    private List<Button> diceButtons;

    private ExecutorService messageHandlerPool;

    /* ----------------- Initializer --------------------- */

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        self = this;
        config = ConfigFactory.create(ApplicationConfig.class);
        screenHelper = new ScreenHelper();
        screenTitle.setText(config.boardViewTitle());

        BasicThreadFactory messagePoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Board Message Handler #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        messageHandlerPool = Executors.newSingleThreadExecutor(messagePoolFactory);
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
        listenToLocalClient(this.context.getYatziGame().getClient().getMessageHandler());

        // inform the server, this client is ready to get board data
        context.getYatziGame().getClient().send(
                new Transfer(context.getYatziGame().getUserMe().getUserId(), CLIENT_READY)
        );

        // set the correct height for this screen
        AnchorPane anchorPane = (AnchorPane) context.getRootNode();
        anchorPane.setPrefHeight(875D);

        changeBoardAccessibility(true);
        context.getViewHandler().getStatusController().updateStatus("waiting for data..", true);

        // render the ui
        renderPlayerTable();

        // initialize the ui components
        renderUserStats();
        generateDiceArea();

        lightingGray = new Lighting();
        lightingGray.setDiffuseConstant(1.0);
        lightingGray.setDiffuseConstant(1.0);
        lightingGray.setSpecularConstant(0.0);
        lightingGray.setSpecularExponent(0.0);
        lightingGray.setSurfaceScale(0.0);
        lightingGray.setLight(new Light.Distant(45, 45, Color.WHITE));

        // define a background
        Image sceneBackgroundImage = screenHelper.getImage(this.context.getClassloader(), "background/", "board_background", "jpg");

        // define background image
        BackgroundImage sceneBackground = new BackgroundImage(
                sceneBackgroundImage,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, new BackgroundPosition(Side.RIGHT, 0, true, Side.TOP, 0, false),
                new BackgroundSize(1400, 900, false, false, false, false));

        anchorPane.setBackground(new Background(sceneBackground));


        // update status bar
        this.context.getViewHandler().getStatusController().updateServeMode(context.getYatziGame().getServeType());

    }

    /* ----------------- UI Rendering --------------------- */

    /**
     * Renders the Player Table
     */
    @SuppressWarnings("unchecked")
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

        // set row factory to display active user on board
        tblPlayers.setRowFactory(tableView -> new UserRow(this.context));
    }

    /**
     * Loads the user data from the game and add the items to the table.
     */
    public void generatePlayerTable() {

        if (context.getYatziGame() != null) {

            LOGGER.debug("{} users are active", context.getYatziGame().getPlayers().size());
            List<User> users = context.getYatziGame().getPlayers();

            // check if the local user is the active user
            if (context.getYatziGame().getActiveUserId().equals(context.getYatziGame().getUserMe().getUserId())) {
                changeDiceAccessibility(false);
            } else {
                changeDiceAccessibility(true);
            }

            // reset the table first
            tblPlayers.getItems().clear();

            for (User u : users) {
                tblPlayers.getItems().add(u);
            }

            tblPlayers.refresh();

        } else {
            LOGGER.error("The reference to the main controller could not be accessed by the board controller");
        }
        LOGGER.debug("users loaded");
    }

    /**
     * Generates the game board table with the user and their value overview.
     */
    @SuppressWarnings("unchecked")
    public void generateBoardTable() {

        LOGGER.debug("initialize board table");

        tblBoardMain.getColumns().clear();
        tblBoardMain.getItems().clear();

        if (boardTableRows == null)
            boardTableRows = FXCollections.observableArrayList();

        // generate the table raw data list. based on this list, the data will be rendered
        generateBoardTableRows();

        // static combinations
        TableColumn<BoardTableRow, VBox> fieldsColumn = new TableColumn("Fields");

        fieldsColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(
                new VBox(new Label(c.getValue().getField().getFieldTypeHumanReadable()),
                        getFieldTypeImageGroup(c.getValue().getField().getFieldType())
                )
        ));

        fieldsColumn.setSortable(false);
        tblBoardMain.getColumns().add(fieldsColumn);

        // add a user wrapper column
        TableColumn userContainer = new TableColumn("Players");
        userContainer.setSortable(false);
        List<User> users = context.getYatziGame().getPlayers();

        // attach the value service from the board
        ScoreService scoreService = context.getYatziGame().getBoard().getScoreService();

        for (int i = 0; i < users.size(); i++) {

            User userToLoad = users.get(i);
            TableColumn<BoardTableRow, String> userColumn = new TableColumn(userToLoad.getUserName());
            userColumn.setSortable(false);

            userColumn.setCellValueFactory(c -> new SimpleStringProperty(
                    scoreService.getScoreDisplayValue(
                            userToLoad.getUserId(), c.getValue().getField().getFieldType()
                    )
            ));

            UUID activeUserOnBoard = context.getYatziGame().getActiveUserId();
            LOGGER.debug("active user on baord is: {}", activeUserOnBoard);

            // set column styles
            if (userToLoad.getUserId().equals(activeUserOnBoard)) {
                userColumn.setStyle("-fx-background-color: rgba(235,242,216,0.7);");
            } else {
                userColumn.setStyle("-fx-background-color: rgba(229,229,229,0.7);");
            }

            userColumn.getStyleClass().add("bold");

            userColumn.setEditable(false);

            userContainer.getColumns().add(userColumn);
        }

        // user column to main table
        tblBoardMain.getColumns().add(userContainer);

        // add action column
        TableColumn<BoardTableRow, ActionField> actionColumn = this.getActionColumn();
        actionColumn.setSortable(false);
        tblBoardMain.getColumns().add(actionColumn);

        // fill the table with the board table items
        tblBoardMain.getItems().addAll(boardTableRows);
    }

    /**
     * Loads all users statistics about the current user
     */
    private void renderUserStats() {
        // Set Current User
        currentUser.setText(context.getYatziGame().getUserMe().getUserName());
        // Set Current User ID
        String userId = context.getYatziGame().getUserMe().getShortUserId();
        currentUserId.setText("ID  " + userId);
    }

    /**
     * Generates the diceMap area. this means 5 diceMap and one roll button
     */
    private void generateDiceArea() {

        ObservableList<Node> nodes = diceContainer.getChildren();
        this.diceButtons = new ArrayList<>();

        for (Node node : nodes) {

            // check if the node is element of button
            if (node instanceof Button) {

                Button button = (Button) node;
                Integer buttonId = getButtonIdFromString(button.getId());

                if (buttonId != null) {
                    this.diceButtons.add(button);
                    button.setGraphic(screenHelper.renderImageView(
                            context.getClassloader(),
                            "dice/3d/",
                            "dice_default",
                            "jpg",
                            40D,
                            40D));
                }
            }
        }

        btnRollTheDice.setGraphic(screenHelper.renderImageView(
                context.getClassloader(),
                "icons/",
                "roll",
                "jpg",
                40D,
                45D));

    }

    /* ----------------- UI Helpers ----------------------- */

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
     * Generates all board table rows. This list represents the raw btw. src. data of the table view.
     */
    private void generateBoardTableRows() {
        if (this.boardTableRows.size() == 0) {
            for (FieldType fieldType : FieldType.values()) {

                if (fieldType == FieldType.SUB_TOTAL || fieldType == FieldType.TOTAL) {
                    this.boardTableRows.add(new BoardTableRow(
                            new Field(fieldType, 0, true),
                            context.getYatziGame().getPlayers(),
                            new ActionField(false)
                    ));
                } else {
                    this.boardTableRows.add(new BoardTableRow(
                            new Field(fieldType, 0, false),
                            context.getYatziGame().getPlayers(),
                            new ActionField(false)
                    ));
                }
            }
        }
    }

    /**
     * Generates the action column of the yatzi board.
     *
     * @return TableColumn of the yatzi board.
     */
    @SuppressWarnings("unchecked")
    private TableColumn<BoardTableRow, ActionField> getActionColumn() {

        TableColumn<BoardTableRow, ActionField> actionColumn = new TableColumn("Action");

        Callback<TableColumn<BoardTableRow, ActionField>, TableCell<BoardTableRow, ActionField>> cellFactory = new Callback<>() {

            @Override
            public TableCell<BoardTableRow, ActionField> call(final TableColumn<BoardTableRow, ActionField> param) {
                return new ActionCell<>(self);
            }
        };

        actionColumn.setCellValueFactory(new PropertyValueFactory<>("actionField"));
        actionColumn.setCellFactory(cellFactory);
        actionColumn.setMinWidth(120D);

        return actionColumn;

    }

    private void setDiceValueImage(Button button, DiceType diceType) {
        button.setGraphic(screenHelper.renderImageView(
                context.getClassloader(),
                "dice/3d/",
                "dice_" + diceType.toString().toLowerCase(),
                "jpg",
                40D,
                40D));
    }

    /**
     * Gets an HBox with ImageViews inside of a diceMap combination
     *
     * @param fieldType The FieldType of the field
     * @return A HBox JavaFx Node
     */
    private HBox getFieldTypeImageGroup(FieldType fieldType) {

        FieldTypeHelper fieldTypeHelper = new FieldTypeHelper();

        // render the hbox
        HBox imageGroup = new HBox();
        imageGroup.setSpacing(4);

        // get diceMap combination
        List<DiceType> combination = fieldTypeHelper.getDiceCombination(fieldType);

        // loop through all diceMap types in the combination, and get the according image
        for (DiceType diceType : combination) {
            // get the image for the type
            ImageView diceImageView = screenHelper.renderImageView(context.getClassloader(), "dice/",
                    "dice_" + diceType.toString().toLowerCase(), "png", 15D, 15D);
            imageGroup.getChildren().add(diceImageView);
        }

        return imageGroup;
    }

    /**
     * @param buttonString JavaFX Button ID
     * @return The ID of the dice button or null if the id could not be parsed
     */
    private Integer getButtonIdFromString(String buttonString) {
        // parse the String ID to Integer
        final String[] btnIdSplit = buttonString.split(Pattern.quote("_"));

        if (btnIdSplit.length > 1) {
            return Integer.valueOf(btnIdSplit[1]);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private void updateChoiceAction(Map<DiceType, Integer> diceResult) {

        BoardManager boardManager = context.getYatziGame().getBoard().getBoardManager();
        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceResult);

        for (BoardTableRow boardTableRow : boardTableRows) {

            YatziGame game = context.getYatziGame();
            FieldType fieldType = boardTableRow.getField().getFieldType();
            Integer fieldValue = 0;

            // check if this board table has a matching condition
            if (matchingFields.get(boardTableRow.getField().getFieldType()) != null) {

                // only assign choose action it the current field has no value in it
                if (game.getBoard().getScoreService().getScore(game.getUserMe().getUserId(), fieldType) == null) {

                    boardTableRow.getActionField().setHasAction(true);
                    boardTableRow.getActionField().setActionType(ActionType.CHOOSE);
                    fieldValue = matchingFields.get(fieldType);

                    // update the action field
                    boardTableRow.getActionField().setData(fieldValue);
                } else {
                    boardTableRow.getActionField().setHasAction(false);
                    boardTableRow.getActionField().setIsLocked(true);
                }

                LOGGER.debug("the matching field {} has a value of {}", fieldType, fieldValue);

            } else {
                // no matching field found (check if the user can strike one field)
                // TODO: After strike, the field has still a value of 0
                if (game.getBoard().getScoreService().getScore(game.getUserMe().getUserId(), fieldType) == null) {
                    boardTableRow.getActionField().setHasAction(true);
                    boardTableRow.getActionField().setActionType(ActionType.STRIKE);
                } else {
                    boardTableRow.getActionField().setHasAction(false);
                    boardTableRow.getActionField().setActionType(ActionType.NONE);
                }
            }
        }

        // render the board table
        Platform.runLater(() -> this.tblBoardMain.refresh());
    }

    /* ----------------- Actions --------------------- */

    /**
     * Listens to the Input Message Queue from the Server
     *
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
                                context.getYatziGame().getBoard().resetDiceAttemptCounter();

                                Platform.runLater(() -> {
                                    generateGameBoardComponents(false);
                                    context.getViewHandler().getStatusController().updateStatus("round 1 started", StatusType.OK);
                                });

                                break;
                            case GAME_CHANGED:
                                YatziGame game = objectMapper.readValue(transfer.getBody(), YatziGame.class);
                                context.getYatziGame().updateGame(game);

                                // The user have again the initial amount of attempts
                                this.updateTotals();
                                context.getYatziGame().getBoard().resetDiceAttemptCounter();
                                LOGGER.debug("got new game change from another client. Active user on board: {}", game.getActiveUserId());

                                Platform.runLater(() -> {

                                    // reset locked state of dce
                                    for (Button dice : this.diceButtons) {
                                        dice.setEffect(null);
                                    }

                                    generateGameBoardComponents(false);
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
                    Thread.sleep(config.queuePauseLength());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread messageListenerTask = new Thread(messageListener);
        messageHandlerPool.submit(messageListenerTask);
    }

    /**
     * Updates the total of the according fields of all users
     */
    public void updateTotals() {
        for (User user : this.context.getYatziGame().getUserService().getUsers()) {
            this.context.getYatziGame().getBoard().getScoreService().updateTotals(user.getUserId());
        }
    }

    /**
     * Displays an JavaFX Alert Box
     *
     * @param type    Alert.AlertType
     * @param title   Will be displayed in the window title bar
     * @param header  Will be displayed as main text
     * @param content Will be displayed as action text
     */
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
     * Re or Generate the Components on the Board
     * - player table
     * - board table
     */
    private void generateGameBoardComponents(Boolean accessable) {
        this.generatePlayerTable();
        this.generateBoardTable();
        this.diceAttempts.setText(this.context.getYatziGame().getBoard().getDiceAttemptCounter().toString());
        this.changeBoardAccessibility(accessable);
    }

    /**
     * Will be triggered by Server to make a player changed
     */
    public void nextPlayer() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            // disable the tables during game update
            changeBoardAccessibility(true);

            String game = objectMapper.writeValueAsString(context.getYatziGame());
            Transfer transfer = new Transfer(this.context.getYatziGame().getUserMe().getUserId(), GAME_CHANGED, game);
            context.getYatziGame().getClient().send(transfer);

        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to process the json input because of: {}", e.getMessage());
        }
    }

    /* ----------------- Accessibility ------------- */

    /**
     * Toggles the accessibility of the user controls, to prevent the user to make changes when he is not active.
     *
     * @param disabled should the board be disabled
     */
    public void changeBoardAccessibility(Boolean disabled) {

        tblPlayers.setDisable(disabled);
        tblBoardMain.setDisable(disabled);
    }

    /**
     * Changes the visibility of the dice container.
     *
     * @param disabled should the dice controls be disabled
     */
    public void changeDiceAccessibility(Boolean disabled) {
        diceContainer.setDisable(disabled);

        if (diceButtons != null) {
            for (Button diceButton : diceButtons) {
                diceButton.setDisable(disabled);
            }
        }

    }

    @FXML
    public void diceHandler(ActionEvent event) {
        Button button = (Button) event.getSource();
        String btnStringId = button.getId();

        Integer buttonId = getButtonIdFromString(btnStringId);
        int buttonIndex = buttonId - 1;

        List<Dice> dices = context.getYatziGame().getBoard().getDices();

        if (dices.get(buttonIndex).isLocked()) {
            dices.get(buttonIndex).setLocked(false);
            button.setEffect(null);
        } else {
            dices.get(buttonIndex).setLocked(true);
            button.setEffect(lightingGray);
        }
    }

    @FXML
    public void rollAction(ActionEvent event) {

        Integer attempts = this.context.getYatziGame().getBoard().decreaseDiceAttemptCounter();

        // only do the action if the user has more than 0 attempts
        LOGGER.debug("you have {} attempts now", attempts);

        List<Dice> dices = context.getYatziGame().getBoard().getDices();

        for (int i = 0; i < 5; i++) {
            if (!dices.get(i).isLocked()) {
                Button diceButton = diceButtons.get(i);

                // clear the locked view if this state has changed
                diceButton.getStyleClass().clear();
                diceButton.getStyleClass().add(IMAGE_BUTTON_CLASS);

                // generate new random value..
                dices.get(i).rollTheDice();

                if (dices.get(i).getValueAsDiceType() != null) {
                    LOGGER.trace("set dice image {} to dice button {}", dices.get(i).getValueAsDiceType(), diceButtons.get(i));
                    setDiceValueImage(diceButtons.get(i), dices.get(i).getValueAsDiceType());
                } else {
                    LOGGER.warn("failed to get dice value as dice type. current value is {}", dices.get(i).toString());
                }
            }
        }

        Map<DiceType, Integer> diceResult = context.getYatziGame().getBoard().getDiceResult();

        // set the whole map to zero (otherwise it would be "null")
        for (DiceType diceType : DiceType.values()) {
            diceResult.put(diceType, 0);
        }

        // set the current dice result values into the dice result map
        for (int i = 0; i < dices.size(); i++) {
            DiceType valueType = dices.get(i).getValueAsDiceType();
            diceResult.put(valueType, diceResult.get(valueType) + 1);
        }

        LOGGER.debug("dice result map {}", diceResult.toString());

        this.updateChoiceAction(diceResult);

        if (attempts == 0) {
            changeDiceAccessibility(true);
        }

        // update the ui label
        this.diceAttempts.setText(attempts.toString());
    }

    @FXML
    public void exit(ActionEvent e) {

        // exits the game
        context.getYatziGame().exit();

        // TODO: Implment clean exit of the board screen
        screenHelper.showScreen(context, ScreenType.SETUP);
    }

}
