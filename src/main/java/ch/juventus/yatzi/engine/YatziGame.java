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

    private UUID activeUserId;
    private List<UUID> circleRoundPlayed;

    public YatziGame() {
        userService = new UserService();
        board = new Board();
        circleRoundPlayed = new ArrayList<>();
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

    @JsonIgnore
    public User getRandomActiveUeser() {
        List<User> users = userService.getUsers();
        Random randomGenerator = new Random();

        int index = randomGenerator.nextInt(users.size());
        User user = users.get(index);

        return user;
    }

    @JsonIgnore
    public void nextUserInCircle() {
        List<User> users = userService.getUsers();

        // check if all users has played in this circle
        if (circleRoundPlayed.size() >= users.size()) {
            // start a new circle
            circleRoundPlayed.clear();
        }

        for(User u : users) {
            // check if the user has played in this circle.
            if (!circleRoundPlayed.contains(u.getUserId()) && !u.getUserId().equals(activeUserId)) {
                activeUserId = u.getUserId();
                circleRoundPlayed.add(activeUserId);
                break;
            }
        }
    }

    @JsonIgnore
    private void updatePlayers(List<User> users) {
        userService.updateUsers(users);
    }

    @JsonIgnore
    public void updateGame(YatziGame yatziGame) {
        // update all players on board
        updatePlayers(yatziGame.getUserService().getUsers());

        // the first user is allowed to start play
        this.setActiveUserId(yatziGame.getActiveUserId());
    }

}
