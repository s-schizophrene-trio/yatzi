package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.engine.field.Field;
import ch.juventus.yatzi.engine.user.User;
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
    Field descField;

    @NonNull
    List<User> users;

    ActionField actionField;
}
