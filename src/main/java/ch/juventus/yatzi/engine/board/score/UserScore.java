package ch.juventus.yatzi.engine.board.score;

import ch.juventus.yatzi.engine.field.FieldType;
import ch.juventus.yatzi.user.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class UserScore {

    @NonNull
    private FieldType fieldType;

    @NonNull
    private User owner;

}
