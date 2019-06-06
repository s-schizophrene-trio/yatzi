package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.ui.helper.ScreenHelper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCell<T> extends TableCell<BoardTableRow, ActionField> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Button btn;
    ScreenHelper screenHelper;

    public ActionCell() {
        screenHelper = new ScreenHelper();

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
            LOGGER.debug("board-action -> {}", data.getDescField().getFieldType());
        });
        setGraphic(btn);
    }

    @Override
    public void updateItem(ActionField item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || !item.getActionAvailable()) {
            setGraphic(null);
        } else {
            setGraphic(btn);
        }
    }
}
