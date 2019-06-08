package ch.juventus.yatzi.engine.board.score;

import ch.juventus.yatzi.engine.field.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ScoreService {

    /**
     * Persistence of all Scores
     */
    @JsonIgnore
    private Map<UUID, Map<FieldType, Score>> scores;

    public ScoreService() {
        this.scores = new HashMap<>();
    }

    /**
     * Updates an store scores for a user / field combination
     * @param userId Holder of the scores
     * @param fieldType Type of the field, the value relates
     * @param score A Score object with the actual values in it
     */
    public void updateScore(UUID userId, FieldType fieldType, Score score) {

        // create child map
        Map<FieldType, Score> scoreRow = new HashMap<>();
        scoreRow.put(fieldType, score);

        // add the map to the parent mmap
        scores.put(userId, scoreRow);
    }

    /**
     * Gets a Score by User / Field Combination
     * @param userId Holder of the scores
     * @param fieldType Type of the field, the value relates
     * @return A Score object with the actual values in it
     */
    public Integer getScore(UUID userId, FieldType fieldType) {
        try {
            return scores.get(userId).get(fieldType).getValue();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Gets a Map of Field Type and Score of a User
     * @param userId The owner of these scores
     * @return A map of field type / value mapping
     */
    public Map<FieldType, Score> getScoresByUser(UUID userId) {
        return this.scores.get(userId);
    }

    /**
     * Resets the value table
     */
    public void reset() {
        this.scores = new HashMap<>();
    }

}
