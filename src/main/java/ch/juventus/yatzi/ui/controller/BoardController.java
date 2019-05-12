package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.engine.dice.DiceType;
import ch.juventus.yatzi.engine.field.Field;
import ch.juventus.yatzi.engine.field.FieldType;
import ch.juventus.yatzi.engine.field.FieldTypeHelper;
import ch.juventus.yatzi.network.Server;
import ch.juventus.yatzi.ui.helper.ScreenType;
import ch.juventus.yatzi.ui.helper.ServeType;
import ch.juventus.yatzi.ui.interfaces.ScreenController;
import ch.juventus.yatzi.ui.models.BoardTableRow;
import ch.juventus.yatzi.user.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The Board Controller manages the primary Game UI.
 * @author Jan Minder
 */
public class BoardController implements ScreenController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final String VIEW_TITLE = "Yatzi Play Board";

    private MainController mainController;

    @FXML
    private VBox boardContainer;

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

    /* ----------------- Initializer --------------------- */

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Set Screen Title
        screenTitle.setText(VIEW_TITLE);
    }

    /**
     * Initialize the Board Controller after the View is rendered.
     *
     * @param mainController The context of the Main Controller
     */
    @Override
    public void afterInit(MainController mainController) {

        // store the reference to the main context
        this.mainController = mainController;

        // set the correct height for this screen
        this.mainController.getYatziAnchorPane().setPrefHeight(850D);

        // render the ui
        this.renderPlayerTable();

        // initialize the ui components
        this.loadUserStats();
        this.generatePlayerTable();
        this.generateBoardTable();
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
        this.tblPlayers.getColumns().add(userId);

        TableColumn userName = new TableColumn("User");
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        this.tblPlayers.getColumns().add(userName);

        TableColumn<User, ImageView> serveType = new TableColumn("Type");
        // Visualize the Serve Type as Images
        serveType.setCellValueFactory(c -> new SimpleObjectProperty<>(
                this.renderIconImageView(c.getValue().getServeType().toString(), "png")
        ));
        serveType.setStyle("-fx-alignment: CENTER;");
        this.tblPlayers.getColumns().add(serveType);
    }

    /**
     * Renders the Image View based on the Image key and file extension (icon)
     *
     * @param imageKey The unique image name without file extension
     * @param fileExt  The file extension of the image
     * @return An Image View based on the Serve Type with an image loaded and resized it.
     */
    private ImageView renderIconImageView(String imageKey, String fileExt) {
        return this.mainController.renderImageView("icons/", imageKey, fileExt, 20D, 20D);
    }

    /**
     * Renders the Image View based on the Image key and file extension (field)
     *
     * @param imageKey The unique image name without file extension
     * @param fileExt  The file extension of the image
     * @return An Image View based on the Serve Type with an image loaded and resized it.
     */
    private ImageView renderFieldImage(String imageKey, String fileExt) {
        return this.mainController.renderImageView("combinations/", imageKey, fileExt, 20D, 120D);
    }

    /**
     * Loads the user data from the game and add the items to the table.
     */
    public void generatePlayerTable() {
        if (this.mainController.getBoard() != null) {
            LOGGER.debug("{} users are active", this.mainController.getBoard().getUsers().size());

            List<User> users = this.mainController.getBoard().getUsers();

            users.forEach(u -> {
                this.tblPlayers.getItems().add(u);
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
        this.currentUser.setText(this.mainController.getBoard().getCurrentUser().getUserName());
        // Set Current User ID
        String userId = this.mainController.getBoard().getCurrentUser().getShortUserId();
        this.currentUserId.setText("ID  " + userId);
    }

    /**
     * Generates the game board table with the user and their score overview.
     */
    public void generateBoardTable() {

        LOGGER.debug("initialize board table");

        this.boardTableRows = new ArrayList<>();
        for (FieldType fieldType : FieldType.values()) {
            this.boardTableRows.add(new BoardTableRow(new Field(fieldType), this.mainController.getBoard().getUsers()));
        }

        // static combinations
        TableColumn<BoardTableRow, VBox> fieldsContainer = new TableColumn("Fields");

        fieldsContainer.setCellValueFactory(c -> new SimpleObjectProperty<>(
                new VBox(new Label(c.getValue().getDescField().getFieldType().toString().toLowerCase()),
                        this.getFieldTypeImageGroup(c.getValue().getDescField().getFieldType())
                        )
        ));

        this.tblBoardMain.getColumns().add(fieldsContainer);

        // add a user wrapper column
        TableColumn userContainer = new TableColumn("Players");

        List<User> users = this.mainController.getBoard().getUsers();

        for (int i = 0; i < users.size(); i++) {
            // static combinations
            int index = i;
            TableColumn<BoardTableRow, String> userColumn = new TableColumn(users.get(index).getUserName());
            userColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsers().get(index).getShortUserId()));
            userContainer.getColumns().add(userColumn);
        }

        // User column to main table
        this.tblBoardMain.getColumns().add(userContainer);

        // fill the table with the board table items
        this.tblBoardMain.getItems().addAll(this.boardTableRows);

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
            ImageView diceImageView = this.mainController.renderImageView("dice/",
                    "dice_"+diceType.toString().toLowerCase(), "png", 15D, 15D);
            imageGroup.getChildren().add(diceImageView);
        }

        return imageGroup;
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

        switch (this.mainController.getSelectedServeType()) {
            case SERVER:
                this.mainController.getServer().stop();
                break;
            case CLIENT:
                break;
        }

        // TODO: Implment clean exit of the board screen
        this.mainController.showScreen(ScreenType.SETUP);
    }
}
