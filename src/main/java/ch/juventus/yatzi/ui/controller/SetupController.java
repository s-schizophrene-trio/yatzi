package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.helper.ScreenType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class SetupController implements Initializable {

    private MainController mainController;

    @FXML
    private VBox joinServerContainer;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showJoinServerInformation() {
        this.joinServerContainer.setVisible(true);
    }

    public void joinServer() {
        this.mainController.showScreen(ScreenType.BOARD);
    }
}
