package ch.juventus.yatzi.engine;

import ch.juventus.yatzi.engine.board.Board;
import ch.juventus.yatzi.engine.user.User;
import ch.juventus.yatzi.engine.user.UserService;
import ch.juventus.yatzi.network.client.Client;
import ch.juventus.yatzi.network.server.Server;
import ch.juventus.yatzi.ui.enums.ServeType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class YatziGame {

    @NonNull
    private Board board;

    private Client client;
    private Server server;
    private ServeType serveType;

    private UserService userService;

    public YatziGame() {
        userService = new UserService();
        board = new Board();
    }

    /**
     * Gets the Local Users (Unique ID overall)
     * @return The User who is playing this instance of the game
     */
    public User getUserMe() {
        return userService.getLocalUser();
    }

    /**
     * Returns all Players as Users of this Game
     * @return An ArrayList of Users
     */
    public List<User> getPlayers() {
        return userService.getUsers();
    }

}
