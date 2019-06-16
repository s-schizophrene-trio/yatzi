package ch.juventus.yatzi.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config/application.properties" })
public interface ApplicationConfig extends Config {

    /**
     * Defines the time to pause during polling of a queue
     * a range between 100 - 500 can speed up the application
     * a higher range than 500ms saves cpu
     * @return The length of pause in ms
     */
    @DefaultValue("500")
    @Key("queue.read.pause.length")
    int queuePauseLength();

    /**
     * Timeout in seconds the client should wait until the timeout exceeded
     * @return The client timeout as counter
     */
    @DefaultValue("30")
    @Key("client.connection.timeout")
    int clientTimeout();

    /**
     * Title of the board
     * @return The title of the board
     */
    @DefaultValue("Yatzi Game")
    @Key("view.board.title")
    String boardViewTitle();

    /**
     * Amount of attempts to have the possibility to roll the dice
     * @return The amount of attempts as counter
     */
    @DefaultValue("3")
    @Key("game.logic.dice-attempt.max")
    Integer gameLogicDiceAttemptMax();

    /**
     * Maximum progress in percentage of a game state until one user wins
     * @return The maximum between 0 - 100
     */
    @DefaultValue("100")
    @Key("game.win.progress.limit-to-reach")
    Integer gameWinProgressLimitToReach();

    /**
     * Defines the limit, when a user gets the extra scores for his bonus field.
     * @return The min scores to reach, to get the bonus
     */
    @DefaultValue("63")
    @Key("game.logic.bonus.scores.min")
    Integer gameLogicBonusScoresMin();

    /**
     * The amount of points a user gets, when he reach the <pre>gameLogicBonusScoresMin</pre>
     * @return The default bonus scores
     */
    @DefaultValue("35")
    @Key("game.default.bonus")
    Integer gameDefaultBonus();
}
