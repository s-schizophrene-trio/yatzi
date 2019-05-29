package ch.juventus.yatzi.engine.dice;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class Dice {
    private boolean locked;
    private int currentValue;

    public Dice() {
        this.resetDice();
        this.setLocked(false);
    }

    public int rollTheDice() {
        this.currentValue = (int) ((Math.random() * 6) + 1);
        return this.currentValue;
    }

    public void resetDice(){
        this.currentValue = 0;
    }
}
