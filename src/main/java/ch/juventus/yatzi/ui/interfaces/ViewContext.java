package ch.juventus.yatzi.ui.interfaces;

import ch.juventus.yatzi.game.YatziGame;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The View Context interface is used to have a general access to an View Instance
 */
public interface ViewContext {

    ClassLoader getClassloader();
    void setClassloader(ClassLoader classLoader);

    Node getRootNode();
    void setRootNode(Node node);

    Scene getScene();
    void setScene(Scene scene);

    YatziGame getYatziGame();
    void setYatziGame(YatziGame yatziGame);

    Stage getStage();
    void setStage(Stage stage);

    ViewHandler getViewHandler();
    void setViewHandler(ViewHandler viewHandler);

}
