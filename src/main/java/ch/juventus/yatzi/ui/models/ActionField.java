package ch.juventus.yatzi.ui.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class ActionField<T> {

    @NonNull
    private Boolean isActionAvailable;
    private T data;
    private Boolean isLocked;

}
