package ch.juventus.yatzi.game;

import ch.juventus.yatzi.game.board.Board;
import ch.juventus.yatzi.game.user.User;
import ch.juventus.yatzi.game.user.UserService;
import ch.juventus.yatzi.network.client.Client;
import ch.juventus.yatzi.network.server.Server;
import ch.juventus.yatzi.ui.enums.ServeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.*;

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

    /**
     * the currently active user which have access to the board and dice area
     */
    private UUID activeUserId;

    /**
     * all users in this list has finished their play in this circle
     */
    private List<UUID> circleRoundPlayed;

    public YatziGame() {
        userService = new UserService();
        board = new Board(userService);
        circleRoundPlayed = new ArrayList<>();
    }

    /**
     * Gets the Local Users (Unique ID overall)
     *
     * @return The User who is playing this instance of the game
     */
    @JsonIgnore
    public User getUserMe() {
        return userService.getLocalUser();
    }

    /**
     * Returns all Players as Users of this Game
     *
     * @return An ArrayList of Users
     */
    public List<User> getPlayers() {
        return userService.getUsers();
    }

    /**
     * This setter is used for de- and serialization  of the json
     * @param users An arraylist of users.
     */
    public void setPlayers(List<User> users) {
        userService.updateUsers(users);
    }

    /**
     * Updates a YatziGame instance. Including the players and set the active user.
     *
     * @param yatziGame On object of an existing yatzigame
     */
    @JsonIgnore
    public void updateGame(YatziGame yatziGame) {
        // update all players on board
        updatePlayers(yatziGame.getUserService().getUsers());

        // mark the current user as played in this circle
        this.getCircleRoundPlayed().add(yatziGame.getActiveUserId());

        // update score
        this.board.getScoreService().updateScores(yatziGame.getBoard().getScores());

        // the first user is allowed to start play
        this.setActiveUserId(yatziGame.getActiveUserId());
    }

    /**
     * Returns randomly selected user from all registered users.
     *
     * @return A random selected user.
     */
    @JsonIgnore
    public User getRandomActiveUser() {
        List<User> users = userService.getUsers();
        Random randomGenerator = new Random();

        int index = randomGenerator.nextInt(users.size());
        User user = users.get(index);

        return user;
    }

    /**
     * Kick a user out of the game. This could happen if a client closes or exits the game.
     *
     * @param userId The uuid of the user to kick out of the game.
     */
    @JsonIgnore
    public void kickUserFromGame(UUID userId) {
        userService.removeUserById(userId);
        circleRoundPlayed.remove(userId);
    }

    /**
     * Sets the active user to the next user in this circle. Each user can play once in a circle.
     */
    @JsonIgnore
    public void nextUserInCircle() {
        List<User> users = userService.getUsers();

        // check if all users has played in this circle
        if (circleRoundPlayed.size() >= users.size()) {
            // start a new circle
            circleRoundPlayed.clear();
        }

        for (User u : users) {
            // check if the user has played in this circle.
            if (!circleRoundPlayed.contains(u.getUserId()) && !u.getUserId().equals(activeUserId)) {
                activeUserId = u.getUserId();
                circleRoundPlayed.add(activeUserId);
                break;
            }
        }
    }

    /**
     * Sets the Users to an existing list of Users
     *
     * @param users The existing users will be overwritten by a user arrayList
     */
    @JsonIgnore
    private void updatePlayers(List<User> users) {
        userService.updateUsers(users);
    }

    /**
     * Exits a YatziGame. Meaning the server and the client will be shutdown, if available.
     */
    @JsonIgnore
    public void exit() {

        switch (getServeType()) {
            case SERVER:
                getServer().stop();
                break;
            case CLIENT:
                getClient().stop();
                break;
        }

    }

}
