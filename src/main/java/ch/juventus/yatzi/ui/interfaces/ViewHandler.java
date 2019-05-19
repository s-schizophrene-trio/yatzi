package ch.juventus.yatzi.ui.interfaces;

import ch.juventus.yatzi.ui.controller.StatusController;
import ch.juventus.yatzi.ui.helper.ScreenHelper;

/**
 * The ViewHandler extends a normal ViewController with Handling functionality.
 */
public interface ViewHandler extends ViewController  {

    StatusController getStatusController();
    void setStatusController(StatusController statusController);

    ScreenHelper getScreenHelper();
    void setScreenHelper(ScreenHelper screenHelper);

}
