package pp.muza.monopoly.model;

import java.util.List;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.TurnException;

public interface PlayTurn {

    /**
     * Returns the player of the turn.
     *
     * @return the player
     */
    Player getPlayer();

    /**
     * Plays the card.
     *
     * @param actionCard the card to play.
     * @throws TurnException if the player cannot play the card.
     */
    void playCard(ActionCard actionCard) throws TurnException;

    /**
     * Returns true if the turn is finished
     *
     * @return turn finished
     */
    boolean isFinished();

    /**
     * Returns turn information
     *
     * @return turn information
     */
    TurnInfo getTurnInfo();

    /**
     * Returns the list of players
     *
     * @return the list of players
     */
    List<Player> getPlayers();

    /**
     * Ends the turn.
     *
     * @throws TurnException if the turn already finished.
     */
    void endTurn() throws TurnException;

}
