package ch.juventus.yatzi.board;

import ch.juventus.yatzi.engine.dice.Dice;
import ch.juventus.yatzi.ui.helper.ServeType;
import ch.juventus.yatzi.user.User;
import ch.juventus.yatzi.user.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.server.ServerCloneException;
import java.util.ArrayList;
import java.util.List;

import static ch.juventus.yatzi.ui.helper.ServeType.CLIENT;
import static ch.juventus.yatzi.ui.helper.ServeType.SERVER;

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

    // declares the role of the board (server or client)
    private ServeType serveType;

    // instance of the user service
    private UserService userService;

    /**
     * Initialized a new Board based on the serveType. If you request a SERVER board, the board loads during
     * its initialization all remote data from the configured server.
     * @param serveType The serve type of the local game instance. (SERVER or HOST)
     */
    public Board(ServeType serveType) {
        // initialize the server board with default values
        this.initBoard(serveType);
    }

    /**
     * Initialized a new Board based on the default values (server).
     */
    public Board() {
        // initialize the server board with default values
        this.initBoard(CLIENT);
    }

    /**
     * Initializes a new Board with default values configured.
     * @param serveType The Serve Type of the Board (SERVER or CLIENT)
     */
    private void initBoard(ServeType serveType) {
        // init dice and user service
        this.dice = this.initDiceSet(5);
        this.userService = new UserService();

        // init users with the local user
        this.users = new ArrayList<>();

        // add local user
        this.currentUser = userService.generateUser(serveType);
        this.users.add(currentUser);
        // set server type
        this.setServeType(serveType);

        // fetch remote user from server
        if (this.serveType == CLIENT) {
            // add each remote user to the local user list
            this.userService.getRemoteUsers().forEach(this::addUser);
        }

        LOGGER.debug("initialized board {}", this.toString());
    }

    /**
     * This method generates a set of dice used on the board.
     * @param amount The amount of dice in the dice list
     * @return array list with dice in it
     */
    private List<Dice> initDiceSet(int amount) {
        return new ArrayList<>();
    }

    public void addUser(User user) {
        this.users.add(user);
    }
}
