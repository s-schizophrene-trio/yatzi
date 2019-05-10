package ch.juventus.yatzi.ui.controller;

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

    private List<String> testList;

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
                this.renderImageView(c.getValue().getServeType().toString(), "png", 20D, 20D)
        ));
        serveType.setStyle("-fx-alignment: CENTER;");
        this.tblPlayers.getColumns().add(serveType);
    }

    /**
     * Renders the Image View based on the Image key and file extension
     *
     * @param imageKey The unique image name without file extension
     * @param fileExt  The file extension of the image
     * @param height   An optional fit height of the image view
     * @param width    An optional fit width of the image view
     * @return An Image View based on the Serve Type with an image loaded and resized it.
     */
    private ImageView renderImageView(String imageKey, String fileExt, Double height, Double width) {
        ImageView imageView = new ImageView(this.mainController.getImage("icons/", imageKey, fileExt));

        // Resize the Image View if the values are present
        if (height != null) imageView.setFitHeight(height);
        if (width != null) imageView.setFitWidth(width);

        return imageView;
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
        this.currentUserId.setText("id: " + userId);
    }

    /**
     * Generates the game board table with the user and their score overview.
     */
    public void generateBoardTable() {
        LOGGER.debug("initialize board table");

        testList = new ArrayList<>();
        testList.add("Test 0");
        testList.add("Test 1");
        testList.add("Test 2");
        testList.add("Test 3");
        testList.add("Test 4");
        testList.add("Test 5");

        BoardTableRow btr = new BoardTableRow();
        btr.setList1(testList);
        btr.setList2(testList);

        // static fields
        TableColumn<BoardTableRow, String> colFields = new TableColumn("Fields");
        colFields.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getList1().get(0)));

        // add field column to table
        this.tblBoardMain.getColumns().add(colFields);

        // add a user wrapper column
        TableColumn users = new TableColumn("Players");

        this.mainController.getBoard().getUsers().forEach(u -> {
            // static fields
            TableColumn<BoardTableRow, String> userColumn = new TableColumn(u.getUserName());
            userColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getList1().get(0)));
            users.getColumns().add(userColumn);
        });

        // User column to main table
        this.tblBoardMain.getColumns().add(users);

        // fill the table with the board table items
        this.tblBoardMain.getItems().add(btr);

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

    @FXML
    public void exit(ActionEvent e) {
        // TODO: Implment clean exit of the board screen
        this.mainController.showScreen(ScreenType.SETUP);
    }

}
