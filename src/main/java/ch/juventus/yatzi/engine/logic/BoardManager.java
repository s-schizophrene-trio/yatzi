package ch.juventus.yatzi.engine.logic;

import ch.juventus.yatzi.engine.dice.DiceType;
import ch.juventus.yatzi.engine.field.FieldType;

import java.util.HashMap;
import java.util.Map;

public class BoardManager {

    /**
     * Evaluates the combination of the current dice values.
     *
     * @param diceValues The map with all dice combinations in it.
     * @return A list of field types with all matching fields
     */
    public Map<FieldType, Integer> evaluate(Map<DiceType, Integer> diceValues) {

        Map<FieldType, Integer> matchMap = new HashMap<>();

        // functions to check
        checkOnePair(diceValues, matchMap);
        checkTwoPair(diceValues, matchMap);
        checkThreeOfAKind(diceValues, matchMap);
        checkFourOfAKind(diceValues, matchMap);
        checkFullHouse(diceValues, matchMap);
        checkSmallStraight(diceValues, matchMap);
        checkLargeStraight(diceValues, matchMap);
        checkYatzi(diceValues, matchMap);

        return matchMap;
    }

    public void checkOnePair(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 2) {
                // multiplicate index with 2 to calculate pair value
                matchMap.put(FieldType.ONE_PAIR, i * 2);
                break;
            }
        }
    }

    public void checkTwoPair(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        Boolean firstPair = false;
        Boolean secondPair = false;
        int index2 = 0;
        int pairvalue1 = 0;
        int pairvalue2 = 0;


        for (int i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 2) {
                firstPair = true;
                pairvalue1 = i * 2;
                index2 = i - 1;
                break;
            }
        }
        for (int i = index2; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 2) {
                secondPair = true;
                pairvalue2 = i * 2;
                break;
            }
        }

        if (firstPair && secondPair) {
            // multiplicate the indexes with 2 to calculate two pair value
            matchMap.put(FieldType.TWO_PAIRS, pairvalue1 + pairvalue2);
        }
    }

    public void checkThreeOfAKind(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 3) {
                // multiplicate index with 3 to calculate three of a kind value
                matchMap.put(FieldType.THREE_OF_A_KIND, i * 3);
            }
        }
    }

    public void checkFourOfAKind(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 1; i < 6; i++) {
            if (diceValues.get(DiceType.get(i)) >= 4) {
                // multiplicate index with 4 to calculate four of a kind value
                matchMap.put(FieldType.FOUR_OF_A_KIND, i * 4);
            }
        }
    }

    //TODO: check function

    public void checkFullHouse(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        Boolean pair = false;
        Boolean threeOfAKind = false;

        for (int i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 2) {
                pair = true;
            }
        }
        for (int i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 3) {
                threeOfAKind = true;
            }
        }

        if (pair && threeOfAKind) {
            matchMap.put(FieldType.FULL_HOUSE, 25);
        }
    }

    public void checkSmallStraight(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {
        for (int i = 0; i < 3; i++) {
            if (diceValues.get(DiceType.get(i + 1)) >= 1
                    && diceValues.get(DiceType.get(i + 2)) >= 1
                    && diceValues.get(DiceType.get(i + 3)) >= 1
                    && diceValues.get(DiceType.get(i + 4)) >= 1) {
                matchMap.put(FieldType.SMALL_STRAIGHT, 30);
            }
        }
    }

    public void checkLargeStraight(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {
        for (int i = 0; i < 2; i++) {
            if (diceValues.get(DiceType.get(i + 1)) >= 1
                    && diceValues.get(DiceType.get(i + 2)) >= 1
                    && diceValues.get(DiceType.get(i + 3)) >= 1
                    && diceValues.get(DiceType.get(i + 4)) >= 1
                    && diceValues.get(DiceType.get(i + 5)) >= 1) {
                matchMap.put(FieldType.LARGE_STRAIGHT, 40);
            }
        }
    }

    public void checkYatzi(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 5) {
                matchMap.put(FieldType.YATZI, 50);
            }
        }
    }
}
