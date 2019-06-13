package ch.juventus.yatzi.game.board;

import ch.juventus.yatzi.config.ApplicationConfig;
import ch.juventus.yatzi.game.board.score.Score;
import ch.juventus.yatzi.game.board.score.ScoreService;
import ch.juventus.yatzi.game.dice.Dice;
import ch.juventus.yatzi.game.dice.DiceType;
import ch.juventus.yatzi.game.field.FieldType;
import ch.juventus.yatzi.game.logic.BoardManager;
import ch.juventus.yatzi.game.user.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.aeonbits.owner.ConfigFactory;

import java.util.*;

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

    // This map includes each dice type and the according count of values which are now visible for the user
    @JsonIgnore
    private HashMap<DiceType, Integer> diceResult;

    @JsonIgnore
    private UserService userService;

    @JsonIgnore
    private BoardManager boardManager;

    // user value service
    @JsonIgnore
    private ScoreService scoreService;

    @JsonIgnore
    private ApplicationConfig config;

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private Integer diceAttemptCounter;

    /**
     * Initialized a new Board with default config.
     */
    public Board() {
        this.dices = this.initDiceSet();
        this.boardManager = new BoardManager();
        this.diceResult = new HashMap<>();
        this.scoreService = new ScoreService();
        this.config = ConfigFactory.create(ApplicationConfig.class);
        this.diceAttemptCounter = config.gameLogicDiceAttemptMax();
    }

    /**
     * Decreases the Dice Attempt Counter until it is 0
     */
    public Integer decreaseDiceAttemptCounter() {
        if (this.diceAttemptCounter > 0) {
            this.diceAttemptCounter--;
            return this.diceAttemptCounter;
        }
        return 0;
    }

    /**
     * Resets the dice attempt counter to the max attempts configured in application.properties
     */
    public void resetDiceAttemptCounter() {
        this.diceAttemptCounter = config.gameLogicDiceAttemptMax();
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

    public Map<UUID, Map<FieldType, Score>> getScores() {
        return this.getScoreService().getScores();
    }

    public void setScores(Map<UUID, Map<FieldType, Score>> scores) {
        this.getScoreService().setScores(scores);
    }

}
