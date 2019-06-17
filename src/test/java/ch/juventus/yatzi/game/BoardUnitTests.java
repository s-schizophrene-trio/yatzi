package ch.juventus.yatzi.game;

import ch.juventus.yatzi.game.dice.DiceType;
import ch.juventus.yatzi.game.field.FieldType;
import ch.juventus.yatzi.game.logic.BoardManager;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardUnitTests {

    @Test
    void test_evaluate_method_for_one_pair() {

        // Initiate a new Board Manager
        BoardManager boardManager = new BoardManager();

        // create new list of dice and fill with sample value set
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 2);
        diceValues.put(DiceType.TWO, 0);
        diceValues.put(DiceType.THREE, 1);
        diceValues.put(DiceType.FOUR, 0);
        diceValues.put(DiceType.FIVE, 2);
        diceValues.put(DiceType.SIX, 0);

        // call de evaluate function to test the return value
        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);

        // check if the evaluate method had successfully detected the combinations
        assertTrue(matchingFields.containsKey(FieldType.ONES));
        assertTrue(matchingFields.containsKey(FieldType.THREES));
        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.TWO_PAIRS));

        assertFalse(matchingFields.containsKey(FieldType.TWOS));
        assertFalse(matchingFields.containsKey(FieldType.FOURS));
        assertFalse(matchingFields.containsKey(FieldType.SIXES));
        assertFalse(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));
        assertFalse(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
        assertFalse(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
    }


    @Test
    void test_evaluate_method_for_fours() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 1);
        diceValues.put(DiceType.TWO, 0);
        diceValues.put(DiceType.THREE, 0);
        diceValues.put(DiceType.FOUR, 4);
        diceValues.put(DiceType.FIVE, 0);
        diceValues.put(DiceType.SIX, 0);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);
        assertTrue(matchingFields.containsKey(FieldType.ONES));
        assertTrue(matchingFields.containsKey(FieldType.FOURS));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));
        assertTrue(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));

        assertFalse(matchingFields.containsKey(FieldType.TWOS));
        assertFalse(matchingFields.containsKey(FieldType.THREES));
        assertFalse(matchingFields.containsKey(FieldType.FIVES));
        assertFalse(matchingFields.containsKey(FieldType.SIXES));
        assertFalse(matchingFields.containsKey(FieldType.TWO_PAIRS));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
        assertFalse(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
    }

    @Test
    void test_evaluate_method_for_fives() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 1);
        diceValues.put(DiceType.TWO, 0);
        diceValues.put(DiceType.THREE, 0);
        diceValues.put(DiceType.FOUR, 1);
        diceValues.put(DiceType.FIVE, 3);
        diceValues.put(DiceType.SIX, 0);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);
        assertTrue(matchingFields.containsKey(FieldType.ONES));
        assertTrue(matchingFields.containsKey(FieldType.FOURS));
        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));


        assertFalse(matchingFields.containsKey(FieldType.TWOS));
        assertFalse(matchingFields.containsKey(FieldType.THREES));
        assertFalse(matchingFields.containsKey(FieldType.SIXES));
        assertFalse(matchingFields.containsKey(FieldType.TWO_PAIRS));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
        assertFalse(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
    }

    @Test
    void test_evaluate_method_for_sixes() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 0);
        diceValues.put(DiceType.TWO, 0);
        diceValues.put(DiceType.THREE, 0);
        diceValues.put(DiceType.FOUR, 0);
        diceValues.put(DiceType.FIVE, 3);
        diceValues.put(DiceType.SIX, 2);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);

        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.SIXES));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.TWO_PAIRS));
        assertTrue(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));
        assertTrue(matchingFields.containsKey(FieldType.FULL_HOUSE));

        assertFalse(matchingFields.containsKey(FieldType.ONES));
        assertFalse(matchingFields.containsKey(FieldType.TWOS));
        assertFalse(matchingFields.containsKey(FieldType.THREES));
        assertFalse(matchingFields.containsKey(FieldType.FOURS));
        assertFalse(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));
        assertFalse(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
    }

    @Test
    void test_evaluate_method_four_of_a_kind() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 0);
        diceValues.put(DiceType.TWO, 0);
        diceValues.put(DiceType.THREE, 4);
        diceValues.put(DiceType.FOUR, 0);
        diceValues.put(DiceType.FIVE, 1);
        diceValues.put(DiceType.SIX, 0);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);
        assertTrue(matchingFields.containsKey(FieldType.THREES));
        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));
        assertTrue(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));

        assertFalse(matchingFields.containsKey(FieldType.ONES));
        assertFalse(matchingFields.containsKey(FieldType.TWOS));
        assertFalse(matchingFields.containsKey(FieldType.FOURS));
        assertFalse(matchingFields.containsKey(FieldType.SIXES));
        assertFalse(matchingFields.containsKey(FieldType.TWO_PAIRS));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
        assertFalse(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
    }

    @Test
    void test_evaluate_method_large_straight_starting_with_one() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 1);
        diceValues.put(DiceType.TWO, 1);
        diceValues.put(DiceType.THREE, 1);
        diceValues.put(DiceType.FOUR, 1);
        diceValues.put(DiceType.FIVE, 1);
        diceValues.put(DiceType.SIX, 0);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);
        assertTrue(matchingFields.containsKey(FieldType.ONES));
        assertTrue(matchingFields.containsKey(FieldType.TWOS));
        assertTrue(matchingFields.containsKey(FieldType.THREES));
        assertTrue(matchingFields.containsKey(FieldType.FOURS));
        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertTrue(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));

        assertFalse(matchingFields.containsKey(FieldType.SIXES));
        assertFalse(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertFalse(matchingFields.containsKey(FieldType.TWO_PAIRS));
        assertFalse(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));
        assertFalse(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
    }

    @Test
    void test_evaluate_method_large_straight_starting_with_two() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 0);
        diceValues.put(DiceType.TWO, 1);
        diceValues.put(DiceType.THREE, 1);
        diceValues.put(DiceType.FOUR, 1);
        diceValues.put(DiceType.FIVE, 1);
        diceValues.put(DiceType.SIX, 1);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);
        assertTrue(matchingFields.containsKey(FieldType.TWOS));
        assertTrue(matchingFields.containsKey(FieldType.THREES));
        assertTrue(matchingFields.containsKey(FieldType.FOURS));
        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.SIXES));
        assertTrue(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertTrue(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));

        assertFalse(matchingFields.containsKey(FieldType.ONES));
        assertFalse(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertFalse(matchingFields.containsKey(FieldType.TWO_PAIRS));
        assertFalse(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));
        assertFalse(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
    }

    @Test
    void test_evaluate_method_yatzi() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 0);
        diceValues.put(DiceType.TWO, 0);
        diceValues.put(DiceType.THREE, 0);
        diceValues.put(DiceType.FOUR, 0);
        diceValues.put(DiceType.FIVE, 0);
        diceValues.put(DiceType.SIX, 5);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);

        assertTrue(matchingFields.containsKey(FieldType.SIXES));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.THREE_OF_A_KIND));
        assertTrue(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));
        assertTrue(matchingFields.containsKey(FieldType.YATZI));

        assertFalse(matchingFields.containsKey(FieldType.ONES));
        assertFalse(matchingFields.containsKey(FieldType.TWOS));
        assertFalse(matchingFields.containsKey(FieldType.THREES));
        assertFalse(matchingFields.containsKey(FieldType.FOURS));
        assertFalse(matchingFields.containsKey(FieldType.FIVES));
        assertFalse(matchingFields.containsKey(FieldType.TWO_PAIRS));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
        assertFalse(matchingFields.containsKey(FieldType.SMALL_STRAIGHT));
        assertFalse(matchingFields.containsKey(FieldType.LARGE_STRAIGHT));
    }
}
