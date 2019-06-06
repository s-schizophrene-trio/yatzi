package ch.juventus.yatzi.network.helper;

public class Commands {

    // player commands
    public static final String PLAYER_NEW = "player_new"; // A new player will join the the game.
    public static final String MAX_PLAYERS_REACHED = "max_players_reached"; // The maximum of clients is reached.
    public static final String PLAYER_EXIT = "player_exit"; // A player leaves the game early.

    // client commands
    public static final String CLIENT_READY = "client_ready"; // The client is ready to start a new game.

    // game commands
    public static final String WAIT_FOR_GAME_READY = "wait_for_game_ready"; // The server is waiting for new players to join.
    public static final String GAME_READY = "game_ready"; // The server has started the game.
    public static final String DICE_CHANGED = "dice_changed"; // The diceMap set has been changed.
    public static final String ROUND_START = "round_start"; // Starts a new round of the game.
    public static final String GAME_CHANGED = "board_changed"; // The board has been changed. (includes the currently active user)
    public static final String GAME_END = "game_end"; // The game is finished. The user can exit the game or start a new party.
    public static final String SERVER_EXIT = "server_exit"; // The server closes its application.
}
