package ch.juventus.yatzi.network.model;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Transfer {

    private UUID sender;

    @NonNull
    private String function;
    private String body;
    private Date sentTime;

    /**
     * Initializes a Transfer Object
     * @param sender The identifier of the user, using this client
     * @param function The command to handle the response
     */
    public Transfer(UUID sender, String function) {
        this.sender = sender;
        this.function = function;
        this.sentTime = new Date();
    }

    public Transfer(UUID sender, @NonNull String function, String body) {
        this.sender = sender;
        this.function = function;
        this.body = body;
        this.sentTime = new Date();
    }
}
