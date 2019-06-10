package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.game.field.Field;
import ch.juventus.yatzi.game.user.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardTableRow {

    @NonNull
    Field field;

    @NonNull
    List<User> users;

    ActionField actionField;
}
