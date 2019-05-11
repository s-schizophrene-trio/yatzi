package ch.juventus.yatzi.engine.field;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Field {

    @NonNull
    private FieldType fieldType;
    private Integer value;

}
