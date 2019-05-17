package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.enums.ServeType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Status Controller is placed at the bottom of the View and displays the status to the user
 */
public class StatusController implements ViewController {

    private ViewContext context;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Label statusLabel;

    @FXML
    private Label serverRoleLabel;

    @FXML
    private ImageView progressStatusIcon;

    @FXML
    private ImageView serverRoleIcon;

    private ScreenHelper screenHelper;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.screenHelper = new ScreenHelper();
    }

    /**
     * Initialize the Board Controller after the View is rendered.
     * @param context The context of the Main Controller
     */
    @Override
    public void afterInit(ViewContext context) {
        // store the reference to the main context
        this.context = context;
        this.progress.setVisible(false);
    }

    /**
     * Updates the Status Bar with a new State (used for loading)
     * @param statusMessage The messaged displayed as current state
     * @param isLoading Defines the visibility of the Progress Indicator
     */
    public void updateStatus(String statusMessage, Boolean isLoading) {

        this.resetProgress();

        if (isLoading) {
            this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            this.progress.setVisible(true);
        } else {
            this.progress.setVisible(false);
        }
        this.statusLabel.setText(statusMessage);
    }

    /**
     * Updates the Status Bar with a new State (used after loading)
     * @param statusMessage The messaged displayed as current state
     * @param statusType Defines a status type, visualized by a green or red indicator
     */
    public void updateStatus(String statusMessage, StatusType statusType) {

        this.resetProgress();

        Image okImage = this.screenHelper.getImage(this.context.getClassloader(), "state/", statusType.toString().toLowerCase(), "png");
        this.progressStatusIcon.setImage(okImage);
        this.progressStatusIcon.setVisible(true);

        this.statusLabel.setText(statusMessage);
    }

    /**
     * Updates the Serve Mode in the Statusbar
     * @param serveType The desired serve type of the game
     */
    public void updateServeMode(ServeType serveType) {
        this.resetServeMode();
        this.serverRoleLabel.setText(serveType.toString().toLowerCase());
        Image modeImage = this.screenHelper.getImage(this.context.getClassloader(), "icons/", serveType.toString().toLowerCase(), "png");
        this.serverRoleIcon.setImage(modeImage);
        this.serverRoleIcon.setVisible(true);
    }

    /**
     * Resets the Serve Mode Status
     */
    public void resetServeMode() {
        this.serverRoleLabel.setText("no role");
        this.serverRoleIcon.setVisible(false);
    }

    /**
     * Resets the Status Bar back to the initial state
     */
    public void resetProgress() {
        this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        this.progress.setVisible(false);
        this.progressStatusIcon.setVisible(false);
    }
}
