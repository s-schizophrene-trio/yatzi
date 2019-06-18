package ch.juventus.yatzi.game.user;

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

    /**
     * Converts the uuid of the user to a string
     * @return A string with the according uuid.
     */
    @JsonIgnore
    public String getUserIdAsString() {
            return userId.toString();
    }


    /**
     * Gets a short version of the uuid if a user (first 3 digits)
     * @return A string with the short userId.
     */
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

    /**
     * Compare a user only by its userId
     * @param o object to compare with the current instance
     * @return true if the object is the same or false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return userId.equals(user.userId);
    }

    /**
     * Generates a hashcode only from userId
     * @return a hash based on the userId
     */
    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
