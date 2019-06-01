package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.engine.field.Field;
import ch.juventus.yatzi.engine.user.User;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class BoardTableRow {

    @NonNull
    Field descField;

    @NonNull
    List<User> users;
}
