package ch.juventus.yatzi.engine.logic;

import ch.juventus.yatzi.engine.dice.DiceType;
import ch.juventus.yatzi.engine.field.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardManager {

    List<FieldType> matchingFields;

    // seldom functions

    /**
     * Evaluates the combination of the current dice values.
     * @param diceValues The map with all dice combinations in it.
     * @return A list of field types with all matching fields
     */
    public List<FieldType> evaluate(Map<DiceType, Integer> diceValues) {

        this.matchingFields = new ArrayList<>();

        // functions to check
        checkOnePair(diceValues);
        checkThreeOfAKind(diceValues);
        checkFourOfAKind(diceValues);
        checkSmallStraight(diceValues);
        checkLargeStraight(diceValues);
        checkYatzi(diceValues);
        return matchingFields;
    }

    public void checkOnePair(Map<DiceType, Integer> diceValues) {

        for (int i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 2) {
                matchingFields.add(FieldType.ONE_PAIR);
            }
        }
    }

    public void checkThreeOfAKind (Map<DiceType, Integer> diceValues){

        for (int i=1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 3){
                matchingFields.add(FieldType.THREE_OF_A_KIND);
            }
        }
    }

    public void checkFourOfAKind (Map<DiceType, Integer> diceValues){

        for (int i=1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 4){
                matchingFields.add(FieldType.FOUR_OF_A_KIND);
            }
        }
    }

    public void checkSmallStraight (Map<DiceType, Integer> diceValues) {
        for (int i = 0; i < 3; i++) {
            if (diceValues.get(DiceType.get(i + 1)) >= 1 && diceValues.get(DiceType.get(i + 2)) >= 1
                    && diceValues.get(DiceType.get(i + 3)) >= 1 && diceValues.get(DiceType.get(i + 4)) >= 1) {
                matchingFields.add(FieldType.SMALL_STRAIGHT);
            }
        }
    }

    public void checkLargeStraight (Map<DiceType, Integer> diceValues) {
        for (int i = 0; i < 2; i++) {
            if (diceValues.get(DiceType.get(i + 1)) >= 1
                    && diceValues.get(DiceType.get(i + 2)) >= 1
                    && diceValues.get(DiceType.get(i + 3)) >= 1
                    && diceValues.get(DiceType.get(i + 4)) >= 1
                    && diceValues.get(DiceType.get(i+5)) >= 1)  {

                matchingFields.add(FieldType.LARGE_STRAIGHT);
            }
        }
    }

    public void checkYatzi (Map<DiceType, Integer> diceValues){

        for (int i=1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 5){
                matchingFields.add(FieldType.YATZI);
            }
        }
    }
}
