package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.helper.ScreenType;
import ch.juventus.yatzi.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
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
    private TableView tblBoardMain;

    @FXML
    private Label currentUser;

    @FXML
    private TableView<User> tblPlayers;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        // Set Screen Title
        screenTitle.setText(VIEW_TITLE);

        // Initialize Active Players Table
        TableColumn userName = new TableColumn("User Name");
        tblPlayers.getStyleClass().add("noheader");
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        this.tblPlayers.getColumns().add(userName);

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

        // static fields
        TableColumn colFields = new TableColumn("Fields");

        //colFields.setCellValueFactory(new PropertyValueFactory<>("fieldType"));
        this.tblBoardMain.getColumns().add(colFields);

        this.mainController.getBoard().getUsers().forEach(u -> {
            // static fields
            TableColumn userColumn = new TableColumn(u.getUserName());
            //colFields.setCellValueFactory(new PropertyValueFactory<>("fieldType"));
            this.tblBoardMain.getColumns().add(userColumn);

        });

        for (int i = 0; i <= 10; i++) {
            //this.tblBoardMain.getItems().add(new Faker().gameOfThrones().dragon(), 34);
        }

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
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
