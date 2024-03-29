package ch.juventus.yatzi.game.board.score;

import ch.juventus.yatzi.config.ApplicationConfig;
import ch.juventus.yatzi.game.field.Field;
import ch.juventus.yatzi.game.field.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ScoreService {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Persistence of all Scores
     */
    @JsonIgnore
    private Map<UUID, Map<FieldType, Field>> scores;

    @JsonIgnore
    private ApplicationConfig config;

    /* ----------------- Initializers --------------------- */

    public ScoreService() {
        this.scores = new HashMap<>();
        this.config = ConfigFactory.create(ApplicationConfig.class);
    }

    /* ----------------- Getter --------------------- */

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
            return null;
        }
    }

    /**
     * Translates the score in to a string value
     * @param userId uuid of user to get the score from
     * @param fieldType the field type of the score
     * @return A String with the score as string
     */
    public String getScoreDisplayValue(UUID userId, FieldType fieldType) {

        Integer score = getScore(userId, fieldType);

        if (score != null) {
            return Integer.toString(score);
        } else {
            return "0";
        }
    }

    /**
     * Gets a Map of Field Type and Score of a User
     * @param userId The owner of these scores
     * @return A map of field type / value mapping
     */
    public Map<FieldType, Field> getScoresByUser(UUID userId) {
        return this.scores.get(userId);
    }

    /* ----------------- Update --------------------- */

    /**
     * Updates an store scores for a user / field combination
     * @param userId Holder of the scores
     * @param fieldType Type of the field, the value relates
     * @param field A Field object with the actual values in it
     */
    public void updateScore(UUID userId, FieldType fieldType, Field field) {

        // check if the user has scores or not
        Map<FieldType, Field> fieldTypeScoreMap = scores.get(userId);

        if (fieldTypeScoreMap == null) {
            fieldTypeScoreMap = new HashMap<>();
        }

        fieldTypeScoreMap.put(fieldType, field);

        // update totals
        fieldTypeScoreMap.put(FieldType.SUB_TOTAL, new Field(FieldType.SUB_TOTAL, this.getSubTotal(userId)));
        fieldTypeScoreMap.put(FieldType.TOTAL, new Field(FieldType.TOTAL, this.getTotal(userId)));

        // add the map to the parent mmap
        scores.put(userId, fieldTypeScoreMap);
    }

    /**
     * Updates all calculated fields (sub_total, total and bonus)
     * @param userId The userId from the user to calculate these fields
     */
    public void updateCalculatedFields(UUID userId) {
        Map<FieldType, Field> fieldTypeScoreMap = scores.get(userId);

        if (fieldTypeScoreMap == null) {
            fieldTypeScoreMap = new HashMap<>();
        }

        fieldTypeScoreMap.put(FieldType.SUB_TOTAL, new Field(FieldType.SUB_TOTAL, this.getSubTotal(userId)));
        fieldTypeScoreMap.put(FieldType.TOTAL, new Field(FieldType.TOTAL, this.getTotal(userId)));
        fieldTypeScoreMap.put(FieldType.BONUS, new Field(FieldType.BONUS, this.getBonus(userId)));
    }

    /**
     * Updates the server scores
     * @param changedScores The list with the different scores in it
     */
    public void updateScores(Map<UUID, Map<FieldType, Field>> changedScores) {
       this.scores = changedScores;
    }

    /* ----------------- Calculations --------------------- */

    /**
     * Updates the score storage with the current sums of their fields
     * @param userId The user id to get the subtotal from
     * @return The sub total of the requested user
     */
    public Integer getSubTotal(UUID userId) {

        // calculate  subtotal
        Integer subTotal = 0;

        // definition of sub total group
        List<FieldType> subTotalGroup = new ArrayList<>();
        subTotalGroup.add(FieldType.ONES);
        subTotalGroup.add(FieldType.TWOS);
        subTotalGroup.add(FieldType.THREES);
        subTotalGroup.add(FieldType.FOURS);
        subTotalGroup.add(FieldType.FIVES);
        subTotalGroup.add(FieldType.SIXES);

        for(FieldType fieldType : subTotalGroup){

            if ( this.scores.get(userId) != null) {
                Field field  = this.scores.get(userId).get(fieldType);

                if (field != null) {
                    subTotal+=field.getValue();
                }
            }

        }

        return subTotal;
    }

    /**
     * Updates the score storage with the current sums of their fields
     * @param userId The user id of the user to get the total of
     * @return The total amount of scores in this round
     */
    public Integer getTotal(UUID userId) {

        // calculate  subtotal
        Integer total = 0;

        for(FieldType fieldType : FieldType.values()){

            // ignore all fields which are not part of the score
            switch (fieldType) {
                case TOTAL:
                    break;
                case SUB_TOTAL:
                    break;
                default:
                    if (this.scores.get(userId) != null) {
                        Field field  = this.scores.get(userId).get(fieldType);

                        if (field != null) {
                            LOGGER.debug("Score on User [{}] at field [{}] with value [{}]",
                                    userId,
                                    field.getFieldTypeHumanReadable(),
                                    field.getValue()
                            );
                            total+=field.getValue();
                        }
                    }
                    break;
            }
        }

        return total ;
    }

    @JsonIgnore
    public Integer getBonus(UUID userId) {

        // check against the game rules
        if (this.getSubTotal(userId) >= config.gameLogicBonusScoresMin()){
            LOGGER.debug("Bonus is given");
            return config.gameDefaultBonus();
        } else {
            return 0;
        }
    }

    /* ----------------- Clean Up --------------------- */

    /**
     * Resets the value table
     */
    public void reset() {
        this.scores = new HashMap<>();
    }


}
