package pp.muza.monopoly.model.game;

import java.util.List;

import pp.muza.monopoly.model.actions.ActionCard;

/**
 * It is a turn API, which is used by players to play the game.
 * There are methods to get the active action cards, and to execute the action cards.
 */
public interface TurnPlayer {

    /**
     * Returns the active action cards for the player at the moment.
     *
     * @return the list of active action cards.
     */
    List<ActionCard> getActiveActionCards();

    /**
     * Executes an action card. the result of the execution is true if the action card was executed successfully.
     *
     * @param actionCard the action card to execute.
     * @return true if the action card was executed successfully.
     * @throws TurnException if it is impossible to execute the action card.
     */
    boolean playCard(ActionCard actionCard) throws TurnException;

    /**
     * get turn's status (finished or not).
     *
     * @return true if the turn is finished.
     */
    boolean isFinished();

    /**
     * get current player.
     *
     * @return returns current player.
     */
    Player getPlayer();

    /**
     * get player's status.
     *
     * @return return status.
     */
    PlayerStatus getStatus();

    /**
     * get player's position.
     *
     * @return the position of the player.
     */
    int getPosition();

    /***
     * Return game snapshot.
     * player can use this method to get the game state, which can be used to make a decision about the next action.
     *
     * @return game info.
     */
    GameInfo getGameInfo();

}
