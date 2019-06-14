package ch.juventus.yatzi.game.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Field {

    @NonNull
    private FieldType fieldType;
    private Integer value;

    // is the user able to fill in this field
    private Boolean isCalculated;

    public Field(FieldType fieldType) {
        this.isCalculated = false;
        this.fieldType = fieldType;
    }

    public Field(@NonNull FieldType fieldType, Boolean isCalculated) {
        this.fieldType = fieldType;
        this.isCalculated = isCalculated;
    }

    public Field(@NonNull FieldType fieldType, Integer value) {
        this.fieldType = fieldType;
        this.value = value;

        if (fieldType.equals(FieldType.SUB_TOTAL)) this.isCalculated = true;
        if (fieldType.equals(FieldType.TOTAL)) this.isCalculated = true;
    }

    /**
     * Returns the field type as human readable.
     * @return A string of the according field type
     */
    @JsonIgnore
    public String getFieldTypeHumanReadable() {
        String fieldTypeString = fieldType.toString().toLowerCase();
        fieldTypeString = fieldTypeString.replaceAll("_", " ");
        return fieldTypeString;
    }
}
