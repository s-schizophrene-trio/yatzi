package ch.juventus.yatzi.engine.user;

import ch.juventus.yatzi.ui.enums.ServeType;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Faker faker;

    // Storage of local users
    private Map<UUID, User> users;

    List<User> remoteUsers;

    private User localUser;

    public UserService() {
        faker = new Faker();
        users = new HashMap<>();
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
        generateFakeUsers();
        return new ArrayList<>(users.values());
    }

    public User getLocalUser() {
        if (localUser != null) {
            return localUser;
        } else {
            return null;
        }
    }

    public User getUserById(UUID userId) {
        return users.get(userId);
    }

    /**
     * Registers a user by the local user list
     * @param user The user object of the user to register
     */
    public void registerUser(User user, Boolean isLocal) {
        users.put(user.getUserId(), user);
        if (isLocal) {
            localUser = user;
        }
    }

}
