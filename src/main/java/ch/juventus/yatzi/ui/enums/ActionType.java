package ch.juventus.yatzi.ui.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The Action Type defines the displayed buttons in the board table
 */
public enum ActionType {

   CHOOSE("choose"), STRIKE("strike"), NONE("none");

    private String value;

    // Reverse-lookup map for getting a dicetype from an value
    private static final Map<String, ActionType> lookup = new HashMap<>();

    ActionType(String value) {
        this.value = value;
    }

    static {
        for (ActionType at : ActionType.values()) {
            lookup.put(at.getValue(), at);
        }
    }

    public String getValue() {
        return this.value;
    }

    public static ActionType get(String value) {
        return lookup.get(value);
    }
}
