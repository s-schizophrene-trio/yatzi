package ch.juventus.yatzi.network.model;

import ch.juventus.yatzi.network.helper.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Message {
    @NonNull
    private String message;
    private Object object;
    @NonNull
    private MessageType messageType;
}
