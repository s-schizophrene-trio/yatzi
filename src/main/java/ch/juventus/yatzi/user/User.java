package ch.juventus.yatzi.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {

    private UUID userId;
    private String userName;

    public User() {
        this.userId = UUID.randomUUID();
    }

    public User(String userName) {
        this.userId = UUID.randomUUID();
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
