package ch.juventus.yatzi.network.helper;

public class Commands {

    // player commands

    /** A new player will join the the game. */
    public static final String PLAYER_NEW = "player_new";
    /** The maximum of clients is reached. */
    public static final String MAX_PLAYERS_REACHED = "max_players_reached";
    /** A player leaves the game early. */
    public static final String PLAYER_EXIT = "player_exit";

    // client commands

    /** The client is ready to start a new game. */
    public static final String CLIENT_READY = "client_ready";

    // game commands

    /** The server is waiting for new players to join. */
    public static final String WAIT_FOR_GAME_READY = "wait_for_game_ready";
    /** The server has started the game. */
    public static final String GAME_READY = "game_ready";
    /** The diceMap set has been changed. */
    public static final String DICE_CHANGED = "dice_changed";
    /** Starts a new round of the game. */
    public static final String ROUND_START = "round_start";
    /** The board has been changed. (includes the currently active user) **/
    public static final String GAME_CHANGED = "game_changed";
    /** The game is finished. The user can exit the game or start a new party. */
    public static final String GAME_END = "game_end";
    /** The server closes its application. */
    public static final String SERVER_EXIT = "server_exit";
}
