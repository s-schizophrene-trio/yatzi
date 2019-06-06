package ch.juventus.yatzi.engine.board;

import ch.juventus.yatzi.engine.board.score.UserScore;
import ch.juventus.yatzi.engine.dice.Dice;
import ch.juventus.yatzi.engine.dice.DiceType;
import ch.juventus.yatzi.engine.field.FieldType;
import ch.juventus.yatzi.engine.user.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Board represents the play ground of this game. The board knows all users and the state of the game.
 */
@Getter
@Setter
@ToString
public class Board {

    // holds all the diceMap from the board
    @JsonIgnore
    private List<Dice> dices;

    @JsonIgnore
    private UserService userService;

    // user score service
    @JsonIgnore
    private Map<FieldType, UserScore> scores;

    /**
     * Initialized a new Board with default config.
     */
    public Board() {
        this.dices = this.initDiceSet();
    }

    /**
     * This method generates a set of diceMap used on the board
     * @return array list with diceMap in it
     */
    @JsonIgnore
    private List<Dice> initDiceSet() {

        List<Dice> diceList = new ArrayList<>();

        for(int i=0; i<5; i++) {
            diceList.add(new Dice());
        }

        return diceList;
    }

}
