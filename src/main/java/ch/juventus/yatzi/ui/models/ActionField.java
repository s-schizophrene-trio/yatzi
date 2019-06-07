package ch.juventus.yatzi.ui.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class ActionField {
    @NonNull
    private Boolean isActionAvailable;
    private Boolean isSelected = false;

}
