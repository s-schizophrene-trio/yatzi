package ch.juventus.yatzi.engine.board;

import ch.juventus.yatzi.engine.board.score.UserScore;
import ch.juventus.yatzi.engine.dice.Dice;
import ch.juventus.yatzi.engine.field.FieldType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Board represents the play ground of this game. The board knows all users and the state of the game.
 */
@Getter
@Setter
@ToString
public class Board {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());



    // holds all the dice from the board
    private List<Dice> dice;

    // only the active user should be able to make board interactions
    private UUID activeUserId;

    // user score service
    private Map<FieldType, UserScore> scores;

    /**
     * Initialized a new Board with default config.
     */
    public Board() {
        this.dice = this.initDiceSet(5);
        LOGGER.debug("initialized a new board");
    }

    /**
     * This method generates a set of dice used on the board.
     * @param amount The amount of dice in the dice list
     * @return array list with dice in it
     */
    private List<Dice> initDiceSet(int amount) {
        return new ArrayList<>();
    }

}
