package ch.juventus.yatzi.game.user;

import ch.juventus.yatzi.ui.enums.ServeType;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Faker faker;

    // In Memory Storage of all users
    private Map<UUID, User> users;

    private User localUser;

    /**
     * Initializes a new User service.
     */
    public UserService() {
        faker = new Faker();
        users = new LinkedHashMap<>();

        LOGGER.debug("user service initialized");
    }

    /**
     * Fetches the list of all users joined the host server
     * @return A List of all Users of the game, including the local one!
     */
    public Map<UUID, User> generateFakeUsers() {

        // TODO: Implement Remote User Service
        if (users.size()  <= 1) {
            // Add some Mock Data
            User fakeServer = new User(faker.name().username(), ServeType.SERVER);
            users.put(fakeServer.getUserId(), fakeServer); // server host

            for (int i = 0; i < 1; i++) {
                User fakeClient = new User(faker.name().username(), ServeType.CLIENT);
                users.put(fakeClient.getUserId(), fakeClient); // client host
            }
        }

        return users;
    }

    /**
     * Get all active Users in the whole Game
     * @return A ArrayList with users in it
     */
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Gets the local user if exists
     * @return The local user if exists, otherwise null will be returned
     */
    public User getLocalUser() {
        if (localUser != null) {
            return localUser;
        } else {
            // the user is new and has to be generated
            return null;
        }
    }

    /**
     * Updates all users on the local board.
     * @param newUsers A list of users used for the update (override).
     */
    public void updateUsers(List<User> newUsers) {
        users.clear();
        Map<UUID, User> result1 = newUsers.stream().collect(
                Collectors.toMap(User::getUserId, x -> x));
        users.putAll(result1);
    }

    /**
     * Removes a specific user from the user store.
     * @param userId The id of the user to remove
     */
    public void removeUserById(UUID userId) {
        users.remove(userId);
    }

    /**
     * Gets a user by its uuid
     * @param userId The uuid of the user to return
     * @return The user object with the same uuid.
     */
    public User getUserById(UUID userId) {
        return users.get(userId);
    }

    /**
     * Registers a user by the local user list
     * @param user The user object of the user to register
     * @param isLocal Declares if the user is a local user or a remote user
     * @return Returns the user object of the registered user
     */
    public User registerUser(User user, Boolean isLocal) {
        users.put(user.getUserId(), user);
        if (isLocal) {
            localUser = user;
        }
        return user;
    }

}
