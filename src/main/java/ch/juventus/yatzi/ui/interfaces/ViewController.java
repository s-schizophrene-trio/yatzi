package ch.juventus.yatzi.ui.interfaces;

import javafx.fxml.Initializable;

/**
 * The View Controller Interface is used to force all Controllers to implement needed methods.
 */
public interface ViewController extends Initializable {

    /**
     * This method runs all initializations, which depends on an present loaded view layout.
     * @param context The function of the main controller
     */
    void afterInit(ViewContext context);

}
