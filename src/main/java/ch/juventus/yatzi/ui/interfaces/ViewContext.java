package ch.juventus.yatzi.ui.interfaces;

import ch.juventus.yatzi.engine.YatziGame;
import ch.juventus.yatzi.engine.board.Board;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public interface ViewContext {

    ClassLoader getClassloader();
    void setClassloader(ClassLoader classLoader);

    Node getRootNode();
    void setRootNode(Node node);

    Scene getScene();
    void setScene(Scene scene);

    YatziGame getYatziGame();
    void setYatziGame(YatziGame yatziGame);

    Board getBoard();
    void setBoard(Board board);

    Stage getStage();
    void setStage(Stage stage);

    ViewHandler getViewHandler();
    void setViewHandler(ViewHandler viewHandler);

}
