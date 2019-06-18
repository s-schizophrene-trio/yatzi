package ch.juventus.yatzi.game.board;

import ch.juventus.yatzi.config.ApplicationConfig;
import ch.juventus.yatzi.game.board.score.Ranking;
import ch.juventus.yatzi.game.board.score.ScoreService;
import ch.juventus.yatzi.game.dice.Dice;
import ch.juventus.yatzi.game.dice.DiceType;
import ch.juventus.yatzi.game.field.FieldType;
import ch.juventus.yatzi.game.field.Field;
import ch.juventus.yatzi.game.logic.BoardManager;
import ch.juventus.yatzi.game.user.User;
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
        this.userService = null;
    }

    /**
     * Initialized a new Board with default config and inject a user service.
     * @param userService The User Service is needed to have access to the current user score
     */
    public Board(UserService userService) {
        this.dices = this.initDiceSet();
        this.boardManager = new BoardManager();
        this.diceResult = new HashMap<>();
        this.scoreService = new ScoreService();
        this.config = ConfigFactory.create(ApplicationConfig.class);
        this.diceAttemptCounter = config.gameLogicDiceAttemptMax();
        this.userService = userService;
    }

    /**
     * Initialized a new Board with default config and inject user and score service.
     * @param userService Inject an existing user service
     * @param scoreService Inject an existing score service
     */
    public Board(UserService userService, ScoreService scoreService) {
        this.dices = this.initDiceSet();
        this.boardManager = new BoardManager();
        this.diceResult = new HashMap<>();
        this.scoreService = scoreService;
        this.config = ConfigFactory.create(ApplicationConfig.class);
        this.diceAttemptCounter = config.gameLogicDiceAttemptMax();
        this.userService = userService;
    }

    /**
     * Decreases the Dice Attempt Counter until it is 0
     * @return The current value of the dice attempt counter
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

    /**
     * Calculates the progress of the current Round
     * @return a number between 0 and 100
     */
    @JsonIgnore
    public Long getRoundProgress() {

        // collect all needed infos
        int totalFieldsOccupied = 0;
        int fieldLength = FieldType.values().length;
        int userSize = this.getUserService().getUsers().size();
        int totalFieldsCount = fieldLength * userSize;

        // calculate the amount of filled in fields per user
        for(User user : userService.getUsers()) {

            // does the user already have a map?
            Map<FieldType, Field> userFieldScores =  scoreService.getScores().get(user.getUserId());

            if (userFieldScores != null) {
                // add the size of the map entries to the total field amount value
                totalFieldsOccupied += userFieldScores.size();
            }

        }

        // calculate the percentage of the game progress
        double percentageOfTotalProgress = (totalFieldsOccupied / (double)totalFieldsCount) * 100;

        // round the percentage an return the value
        return Math.round(percentageOfTotalProgress);
    }

    /**
     * Gets the a list of all player ranging sorted by its total value
     * @return A sorted list of player ranking
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public List<Ranking> getPlayerRankings() {

        // copy user list for sorting
        List<Ranking> ranking = new ArrayList<>();
        for(User u : this.userService.getUsers()) {
            ranking.add(new Ranking(0, u.getUserName(), this.scoreService.getTotal(u.getUserId())));
        }

        Collections.sort(ranking);

        // update ranks
        Integer rank = 1;
        for (Ranking r : ranking) {
            r.setRank(rank);
            rank++;
        }

        return ranking;

    }

    /**
     * Gets the scores from the score service
     * @return A two dimensional map of user, field, value combination
     */
    public Map<UUID, Map<FieldType, Field>> getScores() {
        return this.getScoreService().getScores();
    }

    public void setScores(Map<UUID, Map<FieldType, Field>> scores) {
        this.getScoreService().setScores(scores);
    }

}
