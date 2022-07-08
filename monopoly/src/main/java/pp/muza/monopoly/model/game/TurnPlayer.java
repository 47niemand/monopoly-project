package pp.muza.monopoly.model.game;

import java.util.List;

import pp.muza.monopoly.model.actions.ActionCard;

/**
 * There are methods to get the active action cards and to execute an action card.
 * On each turn, the player can use action cards.
 * getActiveActionCards() returns the active action cards for the player at the moment.
 * executeActionCard() executes an action card. the result of the execution is true if the action card was executed successfully.
 * execution of an action card can can spawn new action cards. new action cards are added to the active action cards.
 * isFinished() returns true if the turn is finished.
 * <p>
 * TODO: add a method to get game state, which can be used to make a decision about the next action.
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
    boolean executeActionCard(ActionCard actionCard) throws TurnException;

    /**
     * Returns true if the turn is finished.
     *
     * @return true if the turn is finished.
     */
    boolean isFinished();
}
