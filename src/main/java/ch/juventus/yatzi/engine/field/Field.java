package ch.juventus.yatzi.engine.field;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Field {

    @NonNull
    private FieldType fieldType;
    private Integer value;

    // is the user able to fill in this field
    private Boolean isCalculated;

    public Field(@NonNull FieldType fieldType, Boolean isCalculated) {
        this.fieldType = fieldType;
        this.isCalculated = isCalculated;
    }
}
