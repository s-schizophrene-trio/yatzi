package ch.juventus.yatzi.engine.board;

import ch.juventus.yatzi.engine.board.score.UserScore;
import ch.juventus.yatzi.engine.dice.Dice;
import ch.juventus.yatzi.engine.field.FieldType;
import ch.juventus.yatzi.engine.user.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Board represents the play ground of this game. The board knows all users and the state of the game.
 */
@Getter
@Setter
@ToString
public class Board {

    // holds all the dice from the board
    @JsonIgnore
    private List<Dice> dice;

    @JsonIgnore
    private UserService userService;

    // user score service
    @JsonIgnore
    private Map<FieldType, UserScore> scores;

    /**
     * Initialized a new Board with default config.
     */
    public Board() {
        this.dice = this.initDiceSet();
    }

    /**
     * This method generates a set of dice used on the board
     * @return array list with dice in it
     */
    @JsonIgnore
    private List<Dice> initDiceSet() {
        List<Dice> dices = new ArrayList<>();

        for (int i=0; i < 5; i++) {
            dices.add(new Dice());
        }
        return dices;
    }

}
