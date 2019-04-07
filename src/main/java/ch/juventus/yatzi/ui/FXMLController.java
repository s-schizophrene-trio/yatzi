package ch.juventus.yatzi.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.debug("Initialize FXML Controller");
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Yatzi Application");
    }

}
