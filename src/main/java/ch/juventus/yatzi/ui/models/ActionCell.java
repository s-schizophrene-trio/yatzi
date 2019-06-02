package ch.juventus.yatzi.ui.models;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCell<T> extends TableCell<BoardTableRow, ActionField> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Button btn;

    public ActionCell() {
        btn = new Button("select");
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
