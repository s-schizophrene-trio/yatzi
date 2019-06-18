package ch.juventus.yatzi.game;

import ch.juventus.yatzi.config.ApplicationConfig;
import ch.juventus.yatzi.game.board.score.ScoreService;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScoreUnitTests {

    @Test
    public void test_if_score_service_can_be_initiated() {

        // Create new score service
        ScoreService scoreService = new ScoreService();

        // Test instances of Objects
        assertThat(scoreService.getConfig(), instanceOf(ApplicationConfig.class));
        assertThat(scoreService.getScores(), instanceOf(HashMap.class));
    }

    @Test
    public void test_if_scores_can_be_updated() {

        // Create new score service
        ScoreService scoreService = new ScoreService();

        // Test instances of Objects
        assertThat(scoreService.getConfig(), instanceOf(ApplicationConfig.class));
        assertThat(scoreService.getScores(), instanceOf(HashMap.class));
    }

}
