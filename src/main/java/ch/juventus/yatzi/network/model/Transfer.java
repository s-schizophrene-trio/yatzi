package ch.juventus.yatzi.network.model;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    private UUID sender;
    private String context;
    private String query;
    private String body;
    private Date sentTime;
}
