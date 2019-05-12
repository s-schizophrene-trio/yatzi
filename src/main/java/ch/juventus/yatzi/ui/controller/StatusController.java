package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.helper.StatusType;
import ch.juventus.yatzi.ui.interfaces.ScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Status Controller is placed at the bottom of the Screen and displays the status to the user
 * @author Jan Minder
 */
public class StatusController implements ScreenController {

    private MainController mainController;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Label serverRoleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView progressStatusIcon;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Initialize the Board Controller after the View is rendered.
     * @param mainController The context of the Main Controller
     */
    @Override
    public void afterInit(MainController mainController) {
        // store the reference to the main context
        this.mainController = mainController;
        this.progress.setVisible(false);
    }

    /**
     * Updates the Status Bar with a new State (used for loading)
     * @param statusMessage The messaged displayed as current state
     * @param isLoading Defines the visibility of the Progress Indicator
     */
    public void updateStatus(String statusMessage, Boolean isLoading) {
        // disable the icon
        this.reset();

        if (isLoading) {
            this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            this.progress.setVisible(true);
        } else {
            this.progress.setVisible(false);
        }
        this.statusLabel.setText(statusMessage);
    }

    /**
     * Resets the Status Bar back to the initial state
     */
    public void reset() {
        this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        this.progress.setVisible(false);
        this.progressStatusIcon.setVisible(false);
    }

    /**
     * Updates the Status Bar with a new State (used after loading)
     * @param statusMessage The messaged displayed as current state
     * @param statusType Defines a status type, visualized by a green or red indicator
     */
    public void updateStatus(String statusMessage, StatusType statusType) {

        // reset the icon bar
        this.reset();

        Image okImage = this.mainController.getImage("state/", statusType.toString().toLowerCase(), "png");
        this.progressStatusIcon.setImage(okImage);
        this.progressStatusIcon.setVisible(true);

        this.statusLabel.setText(statusMessage);
    }
}
