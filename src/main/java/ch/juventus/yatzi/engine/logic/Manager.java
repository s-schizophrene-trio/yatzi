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

        return fieldTypes;
    }

    // call functions

    // onePair

    public void checkOnePair(Map<Integer, Integer> diceValues) {

        for (int i = 6; i > 0; i--) {
            if (diceValues.get(i) >= 2) {
                fieldTypes.add(FieldType.ONE_PAIR);
            }
        }
    }




}
