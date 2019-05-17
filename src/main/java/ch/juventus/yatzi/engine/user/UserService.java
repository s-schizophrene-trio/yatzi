package ch.juventus.yatzi.engine.user;

import ch.juventus.yatzi.ui.enums.ServeType;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private Faker faker;

    // Storage of local users
    private Map<UUID, User> users;

    public UserService() {
        this.faker = new Faker();
        this.users = new HashMap<>();
    }

    public User generateUser(ServeType serveType) {
        return new User(this.faker.funnyName().name(), serveType);
    }

    /**
     * Fetches the list of all users joined the host server
     * @return A List of all Users of the game, including the local one!
     */
    public List<User> getRemoteUsers() {
        // TODO: Implement Remote User Service

        List<User> remoteUsers = new ArrayList<>();

        // Add some Mock Data
        remoteUsers.add(new User(this.faker.name().username(), ServeType.SERVER)); // server host
        for (int i = 0; i <1; i++) {
            remoteUsers.add(new User(this.faker.name().username(), ServeType.CLIENT)); // client hosts
        }
        LOGGER.debug("found {} remote users", remoteUsers.size());
        return remoteUsers;
    }

    /**
     * Get all active Users in the whole Game
     * @return A ArrayList with users in it
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(this.users.values());
    }

    public User getUserById(UUID userId) {
        return this.users.get(userId);
    }

    /**
     * Registers a user by the local user list
     * @param user The user object of the user to register
     */
    public void registerUser(User user) {
        this.users.put(user.getUserId(), user);
    }

}
