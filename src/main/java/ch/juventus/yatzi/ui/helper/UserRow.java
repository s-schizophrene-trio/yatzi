package ch.juventus.yatzi.ui.helper;

import ch.juventus.yatzi.game.user.User;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import javafx.scene.control.TableRow;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@RequiredArgsConstructor
@ToString
public class UserRow extends TableRow<User> {


    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @NonNull
    private ViewContext context;


    @Override
    protected void updateItem(User user, boolean empty){
        super.updateItem(user, empty);

        if (!empty || user != null) {

            if (user.getUserId().equals(context.getYatziGame().getActiveUserId())) {
                LOGGER.debug("user {} is active in player list", user.getUserId());
                if (!getStyleClass().contains("active-user-marker")) {
                    getStyleClass().add("active-user-marker");
                    LOGGER.debug("user element class -> {}", getStyleClass().toString());
                }
            } else {
                getStyleClass().removeAll(Collections.singleton("active-user-marker"));
                LOGGER.trace("user {} is inactive in player list", user.getUserId());
            }

        } else {
            getStyleClass().removeAll(Collections.singleton("active-user-marker"));
            LOGGER.trace("the row does not contain any user information and will be rested");
        }



    }

}
