package pp.muza.monopoly.model;

import java.math.BigDecimal;
import java.util.List;

import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.TurnException;

/**
 * It is a turn API, which is used by players to play the game.
 * There are methods to get the active action cards, and to execute the action
 * cards.
 * There are methods to get the current game information to make the turn
 * decision.
 */
public interface TurnPlayer {

    /**
     * Returns the active action cards for the player at the moment.
     *
     * @return the list of active action cards.
     */
    List<ActionCard> getActiveActionCards();

    /**
     * Executes an action card. the result of the execution is true if the action
     * card was executed successfully.
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
     * player can use this method to get the game state, which can be used to make a
     * decision about the next action.
     *
     * @return game info.
     */
    GameInfo getGameInfo();

    /**
     * Calculates the next position of the player.
     *
     * @param distance the distance to move.
     * @return the new position.
     */
    int nextPosition(int distance);

    /**
     * Returns the land at the given position.
     *
     * @param position the position.
     * @return the land.
     */
    Land getLand(int position);

    /**
     * Returns the owner of the property at the given position.
     *
     * @param position the position.
     * @return the owner.
     */
    Player getPropertyOwner(int position);

    /**
     * returns a fine to pay in order to get out of jail.
     *
     * @return amount to pay.
     */
    BigDecimal getJailFine();

    /**
     * Returns the properties of the player.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getProperties();

    /**
     * return the game's start postion
     *
     * @return the start position.
     */
    int getStartPos();

    /**
     * returns all players in the game.
     *
     * @return the players.
     */
    List<Player> getPlayers();

    /**
     * returns all properties on the board.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getAllProperties();

    /**
     * returns the status of the player.
     *
     * @param player the player.
     * @return the status.
     */
    PlayerStatus getPlayerStatus(Player player);
}
