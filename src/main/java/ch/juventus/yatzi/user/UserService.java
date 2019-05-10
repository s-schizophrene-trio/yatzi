package ch.juventus.yatzi.user;

import ch.juventus.yatzi.ui.helper.ServeType;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private Faker faker;

    public UserService() {
        this.faker = new Faker();
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
        for (int i = 0; i <2; i++) {
            remoteUsers.add(new User(this.faker.name().username(), ServeType.CLIENT)); // client hosts
        }
        LOGGER.debug("found {} remote users", remoteUsers.size());
        return remoteUsers;
    }

}
