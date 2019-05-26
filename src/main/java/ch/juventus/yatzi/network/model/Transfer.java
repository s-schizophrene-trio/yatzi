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

}
