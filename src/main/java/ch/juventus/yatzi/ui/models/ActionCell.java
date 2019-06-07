package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.ui.controller.BoardController;
import ch.juventus.yatzi.ui.helper.ScreenHelper;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        btn.setOnAction((ActionEvent event) -> {
            BoardTableRow data = getTableView().getItems().get(getIndex());
            data.getActionField().setIsSelected(true);
            boardController.nextPlayer();
            LOGGER.debug("board-action -> selected {}", data.getField().getFieldType());
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
