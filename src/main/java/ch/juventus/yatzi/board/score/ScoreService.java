package ch.juventus.yatzi.board.score;

import ch.juventus.yatzi.engine.field.FieldType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ScoreService {

    // Holds the scores for all users
    private Map<FieldType, UserScore> scores;

    public ScoreService() {
        this.scores = new HashMap<>();
    }

    public void updateScore() {

    }
}
