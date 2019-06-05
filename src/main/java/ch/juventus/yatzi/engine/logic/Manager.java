package ch.juventus.yatzi.engine.logic;

import ch.juventus.yatzi.engine.field.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Manager {

    List<FieldType> fieldTypes = new ArrayList<>();

    // seldom functions

    public List<FieldType> evaluate(Map<Integer, Integer> diceValues) {

        // functions to check


        checkOnePair(diceValues);
        checkThreeOfAKind(diceValues);
        checkFourOfAKind(diceValues);
        checkSmallStraight(diceValues);
        checkLargeStraight(diceValues);
        checkYatzi(diceValues);
        return fieldTypes;
    }




    public void checkOnePair(Map<Integer, Integer> diceValues) {

        for (int i = 6; i > 0; i--) {
            if (diceValues.get(i) >= 2) {
                fieldTypes.add(FieldType.ONE_PAIR);
            }
        }
    }

    public void checkThreeOfAKind (Map<Integer, Integer> diceValues){

        for (int i=1; i < 7; i++) {
            if (diceValues.get(i) >= 3){
                fieldTypes.add(FieldType.THREE_OF_A_KIND);
            }
        }
    }

    public void checkFourOfAKind (Map<Integer, Integer> diceValues){

        for (int i=1; i < 7; i++) {
            if (diceValues.get(i) >= 4){
                fieldTypes.add(FieldType.FOUR_OF_A_KIND);
            }
        }
    }

    public void checkSmallStraight (Map<Integer, Integer> diceValues) {
        for (int i = 0; i < 3; i++) {
            if (diceValues.get(i + 1) >= 1 && diceValues.get(i + 2) >= 1
                    && diceValues.get(i + 3) >= 1 && diceValues.get(i + 4) >= 1) {
                fieldTypes.add(FieldType.SMALL_STRAIGHT);
            }
        }
    }

    public void checkLargeStraight (Map<Integer, Integer> diceValues) {
        for (int i = 0; i < 2; i++) {
            if (diceValues.get(i + 1) >= 1 && diceValues.get(i + 2) >= 1
                    && diceValues.get(i + 3) >= 1 && diceValues.get(i + 4) >= 1 && diceValues.get(i+5) >= 1)  {
                fieldTypes.add(FieldType.LARGE_STRAIGHT);
            }
        }
    }

    public void checkYatzi (Map<Integer, Integer> diceValues){

        for (int i=1; i < 7; i++) {
            if (diceValues.get(i) >= 5){
                fieldTypes.add(FieldType.YATZI);
            }
        }
    }
}
