package ch.juventus.yatzi.engine.user;

import ch.juventus.yatzi.ui.enums.ServeType;
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

    public User(@NonNull String userName) {
        this.userName = userName;
        this.serveType = ServeType.SERVER;
        assignUserId();
    }

    public User(@NonNull String userName, @NonNull ServeType serveType) {
        this.userName = userName;
        this.serveType = serveType;
        assignUserId();
    }

    public String getUserIdAsString() {
            return userId.toString();
    }


    public String getShortUserId() {
        return userId.toString().substring(0, 3);
    }

    /**
     * Generates a new UUID.randomUUID() and assign it to the user instance.
     */
    public void assignUserId() {
        userId = UUID.randomUUID();
    }
}
