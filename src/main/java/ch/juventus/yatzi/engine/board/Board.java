package ch.juventus.yatzi.engine.board;

import ch.juventus.yatzi.engine.board.score.UserScore;
import ch.juventus.yatzi.engine.dice.Dice;
import ch.juventus.yatzi.engine.field.FieldType;
import ch.juventus.yatzi.network.Client;
import ch.juventus.yatzi.network.Server;
import ch.juventus.yatzi.ui.enums.ServeType;
import ch.juventus.yatzi.engine.user.User;
import ch.juventus.yatzi.engine.user.UserService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ch.juventus.yatzi.ui.enums.ServeType.CLIENT;
import static ch.juventus.yatzi.ui.enums.ServeType.SERVER;

/**
 * The Board represents the play ground of this game. The board knows all users and the state of the game.
 */

@ToString
public class Board {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // holds all active users for the current game
    @Getter
    private List<User> users;

    // holds all the dice from the board
    @Getter
    private List<Dice> dice;

    // the current user is the player wo is in the row
    @Getter
    private User currentUser;

    // only the active user should be able to make board interactions
    @Getter @Setter
    private UUID activeUserId;

    // declares the role of the board (server or client)
    @Setter @Getter
    private ServeType serveType;

    @Setter @Getter
    private Server server;

    @Setter @Getter
    private Client client;

    // instance of the user service
    @Getter
    private UserService userService;

    // user score service
    private Map<FieldType, UserScore> scores;

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
        this.initBoard(SERVER);
    }

    /**
     * Initializes a new Board with default values configured.
     * @param serveType The Serve Type of the Board (SERVER or CLIENT)
     */
    private void initBoard(ServeType serveType) {
        // init dice and user service
        this.dice = this.initDiceSet(5);
        this.userService = new UserService();

        // add local user
        this.currentUser = this.userService.generateUser(serveType);
        this.userService.registerUser(currentUser);

        // set server type
        this.setServeType(serveType);

        // fetch remote user from server
        if (this.serveType == CLIENT) {
            // add each remote user to the local user list
            this.userService.getRemoteUsers().forEach(this::addUser);
        }

        if (this.serveType == SERVER) {
            this.userService.getRemoteUsers().forEach(this::addUser);
        }

        // init users with the local user
        this.users = this.userService.getAllUsers();

        LOGGER.debug("initialized board");
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
        this.userService.registerUser(user);
    }
}
