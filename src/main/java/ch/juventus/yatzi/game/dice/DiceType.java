package ch.juventus.yatzi.game.dice;

import java.util.HashMap;
import java.util.Map;

public enum  DiceType {

    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6);

    private int value;

    // Reverse-lookup map for getting a dicetype from an value
    private static final Map<Integer, DiceType> lookup = new HashMap<>();

    DiceType(int value) {
        this.value = value;
    }

    static {
        for (DiceType dt : DiceType.values()) {
            lookup.put(dt.getValue(), dt);
        }
    }

    public Integer getValue() {
        return this.value;
    }

    public static DiceType get(Integer value) {
        return lookup.get(value);
    }
}
