package pp.muza.monopoly.model;

import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.errors.GameException;

import java.util.List;
import java.util.Map;

/**
 * Game API for players.
 *
 * @author dmytromuza
 */
public interface PlayGame {

    /**
     * returns the current turn
     *
     * @return the current turn
     */
    boolean isGameInProgress();

    /**
     * starts the game
     *
     * @throws GameException if the game already started
     */
    void start() throws GameException;

    /**
     * returns the current turn
     *
     * @return the current turn
     * @throws GameException if the game is not in progress
     */
    PlayTurn getTurn() throws GameException;

    /**
     * returns all players in the game.
     *
     * @return the players.
     */
    List<Player> getPlayers();

    /**
     * returns the info for the given player.
     *
     * @param player the player
     * @return the info
     */
    PlayerInfo getPlayerInfo(Player player);

    /**
     * Returns the list of active action cards, which are available for the player
     * at the moment.
     *
     * @param player the player.
     * @return the list of active action cards.
     */
    List<ActionCard> getActiveCards(Player player);

    /**
     * Returns the list of all action cards, which are available for the player.
     *
     * @param player the player.
     * @return the list of all action cards.
     */
    List<ActionCard> getCards(Player player);

    /**
     * Returns property owners.
     *
     * @return the map of property owners.
     */
    Map<Integer, Player> getPropertyOwners();

    /**
     * Returns the game board.
     *
     * @return the game board.
     */
    Board getBoard();

    /**
     * Returns status of the player.
     *
     * @param player the player.
     * @return the player's status.
     */
    PlayerStatus getPlayerStatus(Player player);

    /**
     * Returns the player's balance.
     *
     * @param player the player.
     * @return the player's balance.
     */
    int getBalance(Player player);

    /**
     * Returns the turn's number.
     *
     * @return the turn's number.
     */
    int getTurnNumber();
}
