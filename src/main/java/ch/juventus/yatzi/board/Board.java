package ch.juventus.yatzi.board;

import ch.juventus.yatzi.engine.dice.Dice;
import ch.juventus.yatzi.user.User;
import ch.juventus.yatzi.user.UserService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The Board represents the play ground of this game. The board knows all users and the state of the game.
 */
@Getter
@Setter
@ToString
public class Board {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // holds all active users for the current game
    private List<User> users;

    // holds all the dice from the board
    private List<Dice> dice;

    // the current user is the player wo is in the row
    private User currentUser;

    // instance of the user service
    private UserService userService;

    public Board() {
        // init dice and user service
        this.dice = this.initDiceSet(5);
        this.userService = new UserService();

        // init users with the local user
        this.users = new ArrayList<>();
        this.users.add(userService.getLocalUser());

        // TODO: First user in list, starts with the game
        this.currentUser = this.users.get(0);

        LOGGER.debug("initalized board {}", this.toString());
    }

    /**
     * This method generates a set of dice used on the board.
     * @return array list with dice in it
     */
    private List<Dice> initDiceSet(int amount) {
        return new ArrayList<>();
    }
}
