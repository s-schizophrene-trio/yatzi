package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.engine.board.score.Score;
import ch.juventus.yatzi.engine.dice.Dice;
import ch.juventus.yatzi.ui.controller.BoardController;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActionCell<T> extends TableCell<BoardTableRow, ActionField> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Button btn;
    private ScreenHelper screenHelper;
    private BoardController boardController;

    public ActionCell(BoardController boardController) {

        this.screenHelper = new ScreenHelper();
        this.boardController = boardController;


        ImageView image = screenHelper.renderImageView(this.getClass().getClassLoader(),
                "icons/",
                "checked",
                "png",
                20D,
                20D
        );

        btn = new Button("choose ", image);
        btn.getStyleClass().add("action-button");

        // action event on action button in table
        btn.setOnAction((ActionEvent event) -> {

            BoardTableRow boardTableRow = getTableView().getItems().get(getIndex());

            LOGGER.debug("board-action -> selected {}", boardTableRow.getField().getFieldType());

            ViewContext context = this.boardController.getContext();

            Score visibleScore = new Score(boardTableRow.getField().getFieldType(), (Integer) boardTableRow.getActionField().getData());
            LOGGER.debug("visible score of {}", visibleScore);

            context.getYatziGame().getBoard().getScoreService().updateScore(
                    context.getYatziGame().getUserMe().getUserId(),
                    boardTableRow.getField().getFieldType(),
                    visibleScore
            );

            LOGGER.debug("board-action -> score {}", visibleScore);

            for (BoardTableRow row : getTableView().getItems()) {
                row.getActionField().setIsActionAvailable(false);
            }

            // reset dice container
            List<Dice> dices = boardController.getContext().getYatziGame().getBoard().getDices();
            for (Dice d : dices) {
                d.setLocked(false);
            }

            // set action field with value to locked
            boardTableRow.getActionField().setIsLocked(true);

            getTableView().refresh();
            LOGGER.debug("board-action -> refresh board table");

            // player switch -------------
            boardController.nextPlayer();
        });

        setGraphic(btn);
    }

    @Override
    public void updateItem(ActionField item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || !item.getIsActionAvailable()) {
            setGraphic(null);
        } else {
            setGraphic(btn);
        }
    }
}
