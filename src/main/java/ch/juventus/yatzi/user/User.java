package ch.juventus.yatzi.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

    private Integer userId;
    private String userName;

    public User() {
        this.userId = null;
        this.userName = "";
    }

    public User(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
