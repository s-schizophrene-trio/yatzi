package ch.juventus.yatzi.engine.user;

import ch.juventus.yatzi.ui.enums.ServeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {

    @NonNull
    private UUID userId;

    @NonNull
    private String userName;

    private ServeType serveType;

    public User() {
        assignUserId();
    }

    public User(String userName) {
        this.userName = userName;
        this.serveType = ServeType.SERVER;
        assignUserId();
    }

    public User(@NonNull String userName, @NonNull ServeType serveType) {
        this.userName = userName;
        this.serveType = serveType;
        assignUserId();
    }

    @JsonIgnore
    public String getUserIdAsString() {
            return userId.toString();
    }


    @JsonIgnore
    public String getShortUserId() {
        return userId.toString().substring(0, 3);
    }

    /**
     * Generates a new UUID.randomUUID() and assign it to the user instance.
     */
    @JsonIgnore
    public void assignUserId() {
        userId = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
