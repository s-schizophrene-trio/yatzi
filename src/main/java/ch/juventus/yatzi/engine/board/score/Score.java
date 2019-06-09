package ch.juventus.yatzi.engine.board.score;

import ch.juventus.yatzi.engine.field.FieldType;
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
