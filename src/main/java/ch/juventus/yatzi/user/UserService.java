package ch.juventus.yatzi.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public User getLocalUser() {
        return new User();
    }

}
