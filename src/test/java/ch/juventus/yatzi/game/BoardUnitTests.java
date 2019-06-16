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
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.TWO_PAIRS));
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
        assertTrue(matchingFields.containsKey(FieldType.FOURS));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
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
        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));
    }

    @Test
    void test_evaluate_method_for_sixes() {

        BoardManager boardManager = new BoardManager();

        // create new list of dice
        Map<DiceType, Integer> diceValues = new HashMap<>();
        diceValues.put(DiceType.ONE, 1);
        diceValues.put(DiceType.TWO, 0);
        diceValues.put(DiceType.THREE, 0);
        diceValues.put(DiceType.FOUR, 0);
        diceValues.put(DiceType.FIVE, 3);
        diceValues.put(DiceType.SIX, 2);

        Map<FieldType, Integer> matchingFields = boardManager.evaluate(diceValues);
        assertTrue(matchingFields.containsKey(FieldType.SIXES));
        assertTrue(matchingFields.containsKey(FieldType.FIVES));
        assertTrue(matchingFields.containsKey(FieldType.ONES));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
        assertFalse(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));

    }

    @Test
    void test_evaluate_method_for_of_a_kind() {

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
        assertTrue(matchingFields.containsKey(FieldType.FOUR_OF_A_KIND));
        assertTrue(matchingFields.containsKey(FieldType.ONE_PAIR));
        assertTrue(matchingFields.containsKey(FieldType.THREES));
        assertFalse(matchingFields.containsKey(FieldType.YATZI));
        assertFalse(matchingFields.containsKey(FieldType.FULL_HOUSE));

    }


}
