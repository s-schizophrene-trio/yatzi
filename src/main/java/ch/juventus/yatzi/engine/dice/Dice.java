package ch.juventus.yatzi.engine.dice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

import java.util.logging.Logger;


@Setter
@Getter
@ToString
public class Dice {

    private boolean locked;
    private int currentValue;

    public Dice() {
        resetDice();
        setLocked(false);
    }

    public void rollTheDice() {
        currentValue = (int) ((Math.random() * 6) + 1);
    }

    private void resetDice(){
        currentValue = 0;
    }

    public DiceType getValueAsDiceType() {
        switch (currentValue) {
            case 1: return DiceType.ONE;
            case 2: return DiceType.TWO;
            case 3: return DiceType.THREE;
            case 4: return DiceType.FOUR;
            case 5: return DiceType.FIVE;
            case 6: return DiceType.SIX;
        }
        return null;
    }
}
