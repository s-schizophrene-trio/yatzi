package ch.juventus.yatzi.game;

import ch.juventus.yatzi.game.user.User;
import ch.juventus.yatzi.game.user.UserService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserUnitTests {

    private static final String USER_NAME = "test-user";

    @Test
    public void test_if_user_service_can_be_initiated() {

        // Create new user service
        UserService userService = new UserService();

        // Test instances of Objects
        assertThat(userService.getUsers(), instanceOf(ArrayList.class));
        assertNull(userService.getLocalUser());
    }

    @Test
    public void test_if_user_can_be_registered() {

        // Create new user service
        UserService userService = new UserService();

        // Register a new user
        UUID uuid = UUID.randomUUID();
        User userToRegister = new User(uuid, USER_NAME);
        userService.registerUser(userToRegister, true);

        // Check if the User was registered
        assertTrue(userService.getUsers().get(0).getUserId().equals(uuid));
        assertTrue(userService.getUsers().get(0).getUserName().equals(USER_NAME));
    }

    @Test
    public void test_if_user_can_be_removed_by_uuid() {

        // Create new user service
        UserService userService = new UserService();

        // Register a new user
        UUID uuid = UUID.randomUUID();
        User userToRegister = new User(uuid, USER_NAME);
        userService.registerUser(userToRegister, true);

        // Remove User from list
        userService.removeUserById(uuid);

        // Check if the User was registered
        assertTrue(userService.getUsers().size() == 0);
    }

    @Test
    public void test_if_user_can_be_get_by_user_id() {

        // Create new user service
        UserService userService = new UserService();

        // Register a new user
        UUID uuid = UUID.randomUUID();
        User userToRegister = new User(uuid, USER_NAME);
        userService.registerUser(userToRegister, true);

        // Check if the User was registered
        assertTrue(userService.getUserById(uuid).getUserId().equals(uuid));
    }

    @Test
    public void test_if_users_can_be_updated() {

        // Create new user service
        UserService userService = new UserService();

        // Register initial users
        for (int i = 0; i < 8; i++) {
            UUID uuid = UUID.randomUUID();
            User userToRegister = new User(uuid, USER_NAME);
            userService.registerUser(userToRegister, true);
        }

        // check if initial state is correct
        assertTrue(userService.getUsers().size() == 8);

        // Update the users with a new list
        List<User> usersToUpdate = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            UUID uuid = UUID.randomUUID();
            User userToRegister = new User(uuid, USER_NAME);
            usersToUpdate.add(userToRegister);
        }

        userService.updateUsers(usersToUpdate);

        // Check if updated list is now active
        assertTrue(userService.getUsers().size() == 4);

    }

}
