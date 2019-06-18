package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Main Controller manages all sub screens and handel as main function for the whole UI controllers.
 */
public class MainController implements ViewHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // global function
    private ViewContext context;

    // ui helper
    @Getter
    @Setter
    private ScreenHelper screenHelper;

    // each controller should be able to manage the status bar
    private StatusController statusController;

    @Override
    public StatusController getStatusController() {
        return this.statusController;
    }

    @Override
    public void setStatusController(StatusController statusController) {
        this.statusController = statusController;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("initialize main controller");
        this.screenHelper = new ScreenHelper();
    }

    @Override
    public void afterInit(ViewContext context) {

        this.context = context;
        // inizialisieren der UI Komponenten
        this.screenHelper.clearScreen(this.context);
        this.context.setViewHandler(this);
        this.screenHelper.showScreen(this.context, ScreenType.SETUP);

        // Set on Close Event
        context.getStage().setOnHiding(event -> Platform.runLater(() -> {
            LOGGER.info("application closed by click to close button");

            if (this.context.getYatziGame().getServeType() != null) {
                switch (this.context.getYatziGame().getServeType()) {
                    case SERVER:
                        this.context.getYatziGame().getServer().stop();
                        break;
                    case CLIENT:
                        this.context.getYatziGame().getClient().stop();
                        break;
                }
            }

            System.exit(0);
        }));
    }

}
