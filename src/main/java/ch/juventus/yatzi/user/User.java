package ch.juventus.yatzi.user;

import ch.juventus.yatzi.ui.helper.ServeType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @NonNull
    private UUID userId;

    @NonNull
    private String userName;

    private ServeType serveType;

    public User(@NonNull String userName) {
        this.userName = userName;
        this.assignUserId();
        this.serveType = ServeType.SERVER;
    }

    public User(@NonNull String userName, @NonNull ServeType serveType) {
        this.userName = userName;
        this.serveType = serveType;
        this.assignUserId();
    }

    public String getShortUserId() {
        return this.userId.toString().substring(0, 3);
    }

    /**
     * Generates a new UUID.randomUUID() and assign it to the user instance.
     */
    public void assignUserId() {
        UUID userId = UUID.randomUUID();
        this.userId = userId;
    }
}
