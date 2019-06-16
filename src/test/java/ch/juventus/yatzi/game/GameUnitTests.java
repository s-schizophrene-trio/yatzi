package ch.juventus.yatzi.game;

import ch.juventus.yatzi.game.board.Board;
import ch.juventus.yatzi.game.user.UserService;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;

public class GameUnitTests {

    @DisplayName("Check if the Yatzi Game can be initiated")
    @Test
    public void test_if_game_can_be_initiated() {

        // Create new Yatzi Game
        YatziGame yatziGame = new YatziGame();

        // Test instances of Objects
        assertThat(yatziGame.getUserService(), instanceOf(UserService.class));
        assertThat(yatziGame.getBoard(), instanceOf(Board.class));
        assertThat(yatziGame.getCircleRoundPlayed(), instanceOf(ArrayList.class));

    }

}
