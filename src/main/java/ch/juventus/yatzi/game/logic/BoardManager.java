package ch.juventus.yatzi.game.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.juventus.yatzi.game.dice.DiceType;
import ch.juventus.yatzi.game.field.FieldType;

import java.util.HashMap;
import java.util.Map;

public class BoardManager {

    /**
     * Evaluates the combination of the current dice values.
     *
     * @param diceValues The map with all dice combinations in it.
     * @return A list of field types with all matching fields
     */

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public Map<FieldType, Integer> evaluate(Map<DiceType, Integer> diceValues) {

        Map<FieldType, Integer> matchMap = new HashMap<>();

        // functions to check
        checkOnes(diceValues, matchMap);
        checkTwos(diceValues, matchMap);
        checkThrees(diceValues, matchMap);
        checkFours(diceValues, matchMap);
        checkFives(diceValues, matchMap);
        checkSixes(diceValues, matchMap);
        checkOnePair(diceValues, matchMap);
        checkTwoPair(diceValues, matchMap);
        checkThreeOfAKind(diceValues, matchMap);
        checkFourOfAKind(diceValues, matchMap);
        checkFullHouse(diceValues, matchMap);
        checkSmallStraight(diceValues, matchMap);
        checkLargeStraight(diceValues, matchMap);
        checkYatzi(diceValues, matchMap);
        checkChance(diceValues, matchMap);
        return matchMap;
    }

    public void checkOnes(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        if (diceValues.get(DiceType.get(1)) >= 1) {

            matchMap.put(FieldType.ONES, diceValues.get(DiceType.get(1)));
            LOGGER.debug("Ones are matching");
        }
    }

    public void checkTwos(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        if (diceValues.get(DiceType.get(2)) >= 1) {

            matchMap.put(FieldType.TWOS, diceValues.get(DiceType.get(2)) * 2);
            LOGGER.debug("Twos are matching");
        }
    }

    public void checkThrees(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        if (diceValues.get(DiceType.get(3)) >= 1) {

            matchMap.put(FieldType.THREES, diceValues.get(DiceType.get(3)) * 3);
            LOGGER.debug("Threes are matching");
        }
    }

    public void checkFours(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        if (diceValues.get(DiceType.get(4)) >= 1) {

            matchMap.put(FieldType.FOURS, diceValues.get(DiceType.get(3)) * 4);
            LOGGER.debug("Fours are matching");
        }
    }

    public void checkFives(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        if (diceValues.get(DiceType.get(5)) >= 1) {

            matchMap.put(FieldType.FIVES, diceValues.get(DiceType.get(5)) * 5);
            LOGGER.debug("Fives are matching");
        }
    }

    public void checkSixes(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        if (diceValues.get(DiceType.get(6)) >= 1) {

            matchMap.put(FieldType.SIXES, diceValues.get(DiceType.get(6)) * 6);
            LOGGER.debug("Sixes are matching");
        }
    }

    public void checkOnePair(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 2) {
                // multiplicate index with 2 to calculate pair value
                matchMap.put(FieldType.ONE_PAIR, i * 2);
                LOGGER.debug("Pair is matching");
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
            LOGGER.debug("Two Pairs are matching");
        }
    }

    public void checkThreeOfAKind(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 3) {
                // multiplicate index with 3 to calculate three of a kind value
                matchMap.put(FieldType.THREE_OF_A_KIND, i * 3);
                LOGGER.debug("Three of a Kind are matching");
            }
        }
    }

    public void checkFourOfAKind(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 4) {
                // multiplicate index with 4 to calculate four of a kind value
                matchMap.put(FieldType.FOUR_OF_A_KIND, i * 4);
                LOGGER.debug("Four of a Kind are matching");
            }
        }
    }

    //TODO: check function

    public void checkFullHouse(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        Boolean pair = false;
        Boolean threeOfAKind = false;
        Integer indexThreeOfAKind = 0;

        for (int i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 3) {
                threeOfAKind = true;
                indexThreeOfAKind = i;
            }
        }

        for (Integer i = 6; i > 0; i--) {
            if (diceValues.get(DiceType.get(i)) >= 2 && (!i.equals(indexThreeOfAKind))) {
                pair = true;
            }
        }

        if (pair && threeOfAKind) {
            matchMap.put(FieldType.FULL_HOUSE, 25);
            LOGGER.debug("Full House is matching");
        }
    }

    public void checkSmallStraight(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {
        for (int i = 0; i < 3; i++) {
            if (diceValues.get(DiceType.get(i + 1)) >= 1
                    && diceValues.get(DiceType.get(i + 2)) >= 1
                    && diceValues.get(DiceType.get(i + 3)) >= 1
                    && diceValues.get(DiceType.get(i + 4)) >= 1) {
                matchMap.put(FieldType.SMALL_STRAIGHT, 30);
                LOGGER.debug("Small Straight is matching");
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
                LOGGER.debug("Large Straight is matching");
            }
        }
    }

    public void checkYatzi(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        for (int i = 1; i < 7; i++) {
            if (diceValues.get(DiceType.get(i)) >= 5) {
                matchMap.put(FieldType.YATZI, 50);
                LOGGER.debug("Yatzi is matching");
            }
        }
    }

    public void checkChance(Map<DiceType, Integer> diceValues, Map<FieldType, Integer> matchMap) {

        Integer chanceValue = 0;

        for (int i = 1; i < 7; i++) {
            chanceValue = diceValues.get(DiceType.get(i)) * i + chanceValue;
        }
        matchMap.put(FieldType.CHANCE, chanceValue);
        LOGGER.debug("Chance is matching");
    }
}
