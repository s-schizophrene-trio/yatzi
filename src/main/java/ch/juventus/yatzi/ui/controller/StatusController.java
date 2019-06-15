package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.enums.ServeType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Status Controller is placed at the bottom of the View and displays the status to the user
 */
public class StatusController implements ViewController {

    private ViewContext context;
    private ScreenHelper screenHelper;

    @FXML
    private ProgressIndicator progress;
    @FXML
    private Label statusLabel;
    @FXML
    private Label serverRoleLabel;
    @FXML
    private Region regionStatusDivider;
    @FXML
    private Label errorMessage;
    @FXML
    private ImageView progressStatusIcon;
    @FXML
    private ImageView serverRoleIcon;


    /* ----------------- Initializer --------------------- */

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.screenHelper = new ScreenHelper();
    }

    /**
     * Initialize the Board Controller after the View is rendered.
     * @param context The function of the Main Controller
     */
    @Override
    public void afterInit(ViewContext context) {
        // store the reference to the main function
        this.context = context;
        this.progress.setVisible(false);
    }

    /* ----------------- Update Status --------------------- */

    /**
     * Updates the Status Bar with a new State (used for loading)
     * @param statusMessage The messaged displayed as current state
     * @param isLoading Defines the visibility of the Progress Indicator
     */
    public void updateStatus(String statusMessage, Boolean isLoading) {

        this.resetError();
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
        this.resetError();
        this.resetProgress();

        Image okImage = this.screenHelper.getImage(this.context.getClassloader(), "state/", statusType.toString().toLowerCase(), "png");
        this.progressStatusIcon.setImage(okImage);
        this.progressStatusIcon.setVisible(true);

        this.statusLabel.setText(statusMessage);
    }

    /**
     * Resets the Status Bar back to the initial state
     */
    private void resetProgress() {
        this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        this.progress.setVisible(false);
        this.progressStatusIcon.setVisible(false);
    }

    /* ----------------- Handle Error Message ----------------- */

    /**
     * Resets the error message field
     */
    private void resetError() {
        this.errorMessage.setVisible(false);
    }

    /**
     * Shows an error of the message
     * @param errorMessage The error message to show
     */
    public void showError(String errorMessage) {

        this.updateStatus("error", StatusType.NOK);

        this.errorMessage.setVisible(true);
        this.regionStatusDivider.setVisible(false);
        this.errorMessage.setText(errorMessage);
    }

    /* ----------------- Handle Serve Mode --------------------- */

    /**
     * Resets the Serve Mode Status
     */
    private void resetServeMode() {
        this.serverRoleLabel.setText("no role");
        this.serverRoleIcon.setVisible(false);
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

        ColorAdjust whitener = new ColorAdjust();
        whitener.setBrightness(1.0);

        this.serverRoleIcon.setEffect(whitener);
        this.serverRoleIcon.setCache(true);
        this.serverRoleIcon.setCacheHint(CacheHint.SPEED);

    }

}
