package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.helper.ScreenType;
import ch.juventus.yatzi.ui.models.BoardTableRow;
import ch.juventus.yatzi.user.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

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
    private TableView<User> tblPlayers;

    private List<String> testList;

    /* ----------------- Initializer --------------------- */

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Set Screen Title
        screenTitle.setText(VIEW_TITLE);

        // Initialize Active Players Table
        //tblPlayers.getStyleClass().add("noheader");

        TableColumn userId = new TableColumn("ID");
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        this.tblPlayers.getColumns().add(userId);

        TableColumn userName = new TableColumn("User");
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        this.tblPlayers.getColumns().add(userName);
    }

    /**
     * Initialize the Board Controller after the View is rendered.
     * @param mainController The context of the Main Controller
     */
    public void afterInit(MainController mainController) {

        // store the reference to the main context
        this.mainController = mainController;

        this.loadUsers();
        this.loadBoardTable();
    }

    public void loadUsers() {
        if (this.mainController.getBoard() != null) {
            LOGGER.debug("{} users are active", this.mainController.getBoard().getUsers().size());

            List<User> users = this.mainController.getBoard().getUsers();

            users.forEach(u -> {
                this.tblPlayers.getItems().add(u);
            });

            // Set Current User
            this.currentUser.setText("You are: " + this.mainController.getBoard().getCurrentUser().getUserName());

        } else {
            LOGGER.error("The reference to the main controller could not be accessed by the board controller");
        }
        LOGGER.debug("users loaded");
    }

    public void loadBoardTable() {
        LOGGER.debug("initialize board table");

        testList = new ArrayList<>();
        testList.add("Test 0");
        testList.add("Test 1");
        testList.add("Test 2");

        BoardTableRow btr = new BoardTableRow();
        btr.setList1(testList);
        btr.setList2(testList);

        // static fields
        TableColumn<BoardTableRow, String> colFields = new TableColumn("Fields");
        colFields.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getList1().get(0)));

        //colFields.setCellValueFactory(new PropertyValueFactory<>("fieldType"));
        this.tblBoardMain.getColumns().add(colFields);

        this.mainController.getBoard().getUsers().forEach(u -> {
            // static fields
            TableColumn<BoardTableRow, String> userColumn = new TableColumn(u.getUserName());
            userColumn.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getList1().get(0)));
            this.tblBoardMain.getColumns().add(userColumn);

        });

        this.tblBoardMain.getItems().add(btr);

    }

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
