package ch.juventus.yatzi.ui.interfaces;

import ch.juventus.yatzi.ui.controller.MainController;
import javafx.fxml.Initializable;

/**
 * The Screen Controller Interface is used to force all Controllers to implement needed methods.
 * @author Jan Minder
 */
public interface ScreenController extends Initializable {

    /**
     * This method runs all initializations, which depends on an present loaded view layout.
     * @param mainController The context of the main controller
     */
    void afterInit(MainController mainController);
}
