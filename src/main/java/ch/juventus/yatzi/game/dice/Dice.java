package ch.juventus.yatzi.game.dice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class Dice {

    private boolean locked;
    private int currentValue;

    /**
     * Initializes a new Dice
     */
    public Dice() {
        resetDice();
        setLocked(false);
    }

    /**
     * Assigns a new random number between 1 - 6 to this dice
     */
    public void rollTheDice() {
        currentValue = (int) ((Math.random() * 6) + 1);
    }

    /**
     * Resets this dice to 0
     */
    private void resetDice(){
        currentValue = 0;
    }

    /**
     * Converts the dice type to a string value
     * @return The current dice type as string
     */
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
