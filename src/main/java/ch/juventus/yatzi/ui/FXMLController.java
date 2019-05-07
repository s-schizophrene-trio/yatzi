package ch.juventus.yatzi.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @FXML
    private Label label;

    @FXML
    private TableView tblBoardMain;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.debug("initialize fxml controller");
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Yatzi Application");

        // init table
        TableColumn col = new TableColumn("Dynamic Column");
        this.tblBoardMain.getColumns().add(col);
    }


    @FXML
    private void showMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Here...");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                LOGGER.debug("pressed OK");
            }
        });
    }
}
