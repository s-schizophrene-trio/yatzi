package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.game.YatziGame;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The FX Context is an implementation of the View Context and is able to hold a JavaFX specific View Context.
 */
public class FXContext implements ViewContext {

    private ClassLoader classLoader;
    private Node rootNode;
    private Stage stage;
    private Scene scene;
    private ViewHandler viewHandler;
    private YatziGame yatziGame;

    @Override
    public ClassLoader getClassloader() {
        return this.classLoader;
    }

    @Override
    public void setClassloader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Node getRootNode() {
        return this.rootNode;
    }

    @Override
    public void setRootNode(Node node) {
        this.rootNode = node;
    }

    @Override
    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    @Override
    public void setViewHandler(ViewHandler viewHandler) {
        this.viewHandler = viewHandler;
    }

    @Override
    public Scene getScene() {
        return this.scene;
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public YatziGame getYatziGame() {
        return this.yatziGame;
    }

    @Override
    public void setYatziGame(YatziGame yatziGame) {
        this.yatziGame = yatziGame;
    }
}
