package ch.juventus.yatzi.game.field;

import ch.juventus.yatzi.game.dice.DiceType;
import java.util.ArrayList;
import java.util.List;

public class FieldTypeHelper {

    /**
     * Returns a list of dice combinations based on the fieldtype. This is used to generate sample images of a specific combination.
     * @param fieldType The field type to generate a sample combination
     * @return A list of dice types
     */
    public List<DiceType> getDiceCombination(FieldType fieldType) {

        List<DiceType> diceGroup = new ArrayList<>();

        switch (fieldType) {
            case ONES:
                diceGroup.addAll(getDicePair(DiceType.ONE, 5));
                break;
            case TWOS:
                diceGroup.addAll(getDicePair(DiceType.TWO, 5));
                break;
            case THREES:
                diceGroup.addAll(getDicePair(DiceType.THREE, 5));
                break;
            case FOURS:
                diceGroup.addAll(getDicePair(DiceType.FOUR, 5));
                break;
            case FIVES:
                diceGroup.addAll(getDicePair(DiceType.FIVE, 5));
                break;
            case SIXES:
                diceGroup.addAll(getDicePair(DiceType.SIX, 5));
                break;
            // Special Combinations
            case THREE_OF_A_KIND:
                diceGroup.addAll(getDicePair(DiceType.TWO, 3));
                break;
            case FOUR_OF_A_KIND:
                diceGroup.addAll(getDicePair(DiceType.TWO, 4));
                break;
            case SMALL_STRAIGHT:
                diceGroup.add(DiceType.ONE);
                diceGroup.add(DiceType.TWO);
                diceGroup.add(DiceType.THREE);
                diceGroup.add(DiceType.FOUR);
                break;
            case LARGE_STRAIGHT:
                diceGroup.add(DiceType.TWO);
                diceGroup.add(DiceType.THREE);
                diceGroup.add(DiceType.FOUR);
                diceGroup.add(DiceType.FIVE);
                diceGroup.add(DiceType.SIX);
                break;
        }

        return diceGroup;
    }

    /**
     * Returns a list of the same dice types
     * @param diceType The dice type to generate the list
     * @param size The amount of dices to add.
     * @return A List of the same combinations based on the type and the count.
     */
    public List<DiceType> getDicePair(DiceType diceType, int size) {
        List<DiceType> diceTypes = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            diceTypes.add(diceType);
        }
        return diceTypes;
    }

}
