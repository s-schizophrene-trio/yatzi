package ch.juventus.yatzi.game.board.score;

import ch.juventus.yatzi.game.field.FieldType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Score {

    @NonNull
    private FieldType fieldType;

    @NonNull
    private int value;

    public Score(Integer value) {
        this.value = value;
        this.fieldType = null;
    }

    public Score(FieldType fieldType) {
        this.value = 0;
        this.fieldType = fieldType;
    }

}
