package ch.juventus.yatzi.ui.helper;

import ch.juventus.yatzi.game.dice.Dice;
import ch.juventus.yatzi.game.field.Field;
import ch.juventus.yatzi.ui.controller.BoardController;
import ch.juventus.yatzi.ui.enums.ActionType;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.models.ActionField;
import ch.juventus.yatzi.ui.models.BoardTableRow;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActionCell<T> extends TableCell<BoardTableRow, ActionField> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Button chooseButton;
    private final Button strikeButton;
    private ViewContext context;

    private BoardController boardController;

    public ActionCell(BoardController boardController) {

        ScreenHelper screenHelper = new ScreenHelper();
        this.context = boardController.getContext();
        this.boardController = boardController;


        ImageView chooseImage = screenHelper.renderImageView(this.getClass().getClassLoader(),
                "icons/",
                "choose",
                "png",
                20D,
                20D
        );

        ImageView strikeImage = screenHelper.renderImageView(this.getClass().getClassLoader(),
                "icons/",
                "strike",
                "png",
                20D,
                20D
        );

        // initialize a new choose button
        chooseButton = new Button("choose", chooseImage);
        chooseButton.setId(ActionType.CHOOSE.getValue());
        chooseButton.getStyleClass().add("choose-button");

        // initialize a new strike button
        strikeButton = new Button("strike", strikeImage);
        strikeButton.setId(ActionType.STRIKE.getValue());
        strikeButton.getStyleClass().add("strike-button");

        // action event on choose button in table
        chooseButton.setOnAction(this::boardAction);

        // action event on strike button in table
        strikeButton.setOnAction(this::boardAction);

        setGraphic(chooseButton);
    }

    @Override
    public void updateItem(ActionField item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            if (!item.getIsLocked() || item.getHasAction()) {
                switch (item.getActionType()) {
                    case CHOOSE:
                        setGraphic(chooseButton);
                        break;
                    case STRIKE:
                        setGraphic(strikeButton);
                        break;
                    default:
                        setGraphic(null);
                        break;
                }
            } else {
                setGraphic(null);
            }
        }

    }

    private void boardAction(ActionEvent event) {

        Button clickedButton = (Button) event.getSource();
        BoardTableRow boardTableRow = getTableView().getItems().get(getIndex());
        LOGGER.debug("board-action [{}] -> {}", clickedButton.getId(), boardTableRow.getField().getFieldType());

        Field field = new Field(boardTableRow.getField().getFieldType());

        // if the user wants to strike one filed, the score will be updated with a value of 0
        if (clickedButton.getId().equals(ActionType.STRIKE.getValue())) {
            field.setValue(0);
        } else {
            field.setValue((Integer) boardTableRow.getActionField().getData());
        }
        LOGGER.debug("score of {}", field);

        // update the score service with the change
        context.getYatziGame().getBoard().getScoreService().updateScore(
                context.getYatziGame().getUserMe().getUserId(),
                boardTableRow.getField().getFieldType(),
                field
        );

        LOGGER.debug("board-action [{}] -> {}", clickedButton.getId(), field);

        // set all actions to unavailable
        for (BoardTableRow row : getTableView().getItems()) {
            row.getActionField().setHasAction(false);
            row.getActionField().setActionType(ActionType.NONE);
        }

        // reset dice container
        List<Dice> dices = boardController.getContext().getYatziGame().getBoard().getDices();
        for (Dice d : dices) {
            d.setLocked(false);
        }

        // set action field with value to locked
        boardTableRow.getActionField().setIsLocked(true);

        getTableView().refresh();
        LOGGER.debug("board-action [{}] -> refresh board table", clickedButton.getId());

        // player switch -------------
        boardController.nextPlayer();
    }
}
