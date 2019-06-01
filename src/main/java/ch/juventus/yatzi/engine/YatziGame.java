package ch.juventus.yatzi.engine;

import ch.juventus.yatzi.engine.board.Board;
import ch.juventus.yatzi.engine.user.User;
import ch.juventus.yatzi.engine.user.UserService;
import ch.juventus.yatzi.network.client.Client;
import ch.juventus.yatzi.network.server.Server;
import ch.juventus.yatzi.ui.enums.ServeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class YatziGame {

    @NonNull
    private Board board;

    @JsonIgnore
    private Client client;

    @JsonIgnore
    private Server server;

    @JsonIgnore
    private ServeType serveType;

    @JsonIgnore
    private UserService userService;

    public YatziGame() {
        userService = new UserService();
        board = new Board();
    }

    /**
     * Gets the Local Users (Unique ID overall)
     * @return The User who is playing this instance of the game
     */
    @JsonIgnore
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

    public void setPlayers(List<User> users) {
        userService.updateUsers(users);
    }

    public UUID getActiveUserId() {
        return userService.getActiveUserId();
    }

    public void setActiveUserId(UUID userId) {
        this.getUserService().setActiveUserId(userId);
    }

    @JsonIgnore
    private void updatePlayers(List<User> users) {
        userService.updateUsers(users);
    }

    @JsonIgnore
    public void updateGame(YatziGame yatziGame) {
        // update board
        updatePlayers(yatziGame.getUserService().getUsers());
    }

}
