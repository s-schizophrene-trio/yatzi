package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.ui.enums.ActionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActionField<T> {

    @NonNull
    private Boolean hasAction;
    private T data;
    private ActionType actionType;
    private Boolean isLocked;

    public ActionField(@NonNull Boolean hasAction) {
        this.hasAction = hasAction;
        this.isLocked = false;
        this.actionType = ActionType.NONE;
    }

    public ActionField(Boolean hasAction, ActionType type) {
        this.hasAction = hasAction;
        this.actionType = type;
        this.isLocked = false;
    }


}
