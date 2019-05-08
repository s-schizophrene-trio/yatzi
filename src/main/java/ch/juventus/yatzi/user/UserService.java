package ch.juventus.yatzi.user;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private Faker faker;

    public UserService() {
        this.faker = new Faker();
    }

    public User getLocalUser() {
        return new User(this.faker.funnyName().name());
    }

}
