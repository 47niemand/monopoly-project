package pp.muza.monopoly.model;

import java.util.List;
import java.util.NoSuchElementException;

import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.model.game.BaseTurnImpl;

/**
 * Game API for engine.
 *
 * @author dmytromuza
 */
public interface Game {

    /**
     * Returns the id of the jail.
     *
     * @return jail position
     */
    int getJailPosition();

    /**
     * Pops a chance card from the top of the deck.
     *
     * @return the chance card.
     */
    Fortune takeFortuneCard();

    /**
     * Sets the player status to JAIL and moves the player to the jail position.
     *
     * @param player the player.
     * @throws GameException if the player is not in the game.
     */
    void setPlayerInJail(Player player) throws GameException;

    /**
     * Moves the player to the given position and returns the path player was moved on.
     *
     * @param player   the player.
     * @param position the position.
     * @return the path from the current position (excluded) to the new position
     * (included).
     * @throws GameException if the player is not in the game.
     */
    List<Land> moveTo(Player player, int position) throws GameException;

    /**
     * Buys the property at the given position for the player.
     *
     * @param player   the player.
     * @param position the property to buy.
     * @throws GameException if there are some errors (e.g. someone already owns the property).
     * @throws BankException if the player doesn't have enough coins.
     */
    void buyProperty(Player player, int position) throws GameException, BankException;

    /**
     * Leaves the jail.
     *
     * @param player the player who is leaving the jail.
     * @throws GameException if the player is not in jail.
     */
    void leaveJail(Player player) throws GameException;

    /**
     * Sales a property at the given position to the bank.
     *
     * @param player   the player.
     * @param position the property.
     * @throws GameException if there are some errors (e.g. the player does not own the property).
     * @throws BankException if a player cannot receive the coins.
     */
    void doContract(Player player, int position) throws BankException, GameException;

    /**
     * searches for the land's id with the given name.
     * it returns the first land found.
     *
     * @param asset the land name.
     * @return the land id.
     * @throws NoSuchElementException if there is no land with the given name.
     */
    int findProperty(Asset asset);

    /**
     * Returns the list of land's ids with the given color.
     *
     * @param color the color.
     * @return the list of lands.
     */
    List<Integer> findLandsByColor(PropertyColor color);

    /**
     * Sends the action card to the player.
     *
     * @param sender     the player who sends the card.
     * @param to         the player who receives the card.
     * @param actionCard the action card.
     * @throws GameException if recipient cannot receive the card.
     */
    void sendCard(Player sender, Player to, ActionCard actionCard) throws GameException;

    /**
     * Trade a property between two players.
     *
     * @param buyer    the player who is buying the property.
     * @param seller   the player who is selling the property.
     * @param position the property to trade.
     * @throws GameException if there are some errors (e.g. the property is not owned
     *                       by the sale player).
     * @throws BankException if operation fails (e.g. if the player doesn't have
     *                       enough coins).
     */
    void tradeProperty(Player buyer, Player seller, int position) throws BankException, GameException;

    /**
     * Get the number of coins needed to cover the rent.
     *
     * @param position the property.
     * @return the number of coins to pay for the rent, or 0 if anyone does not own the property.
     */
    int getRent(int position);

    /**
     * Adds coins to the player's wallet.
     *
     * @param player the player.
     * @param value  the value of coins to add.
     * @throws BankException if operation fails (e.g. if the player wallet is full).
     */
    void income(Player player, int value) throws BankException;


    /**
     * Returns status of a player.
     *
     * @param player the player.
     * @return the status of the player.
     */
    PlayerStatus getPlayerStatus(Player player);

    /**
     * return the position with the given distance from the current player's
     * position.
     *
     * @param player   the player.
     * @param distance the distance to move.
     * @return the new position.
     */
    int nextPosition(Player player, int distance);


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
     * @return the owner, or null if the property is not owned.
     */
    Player getPropertyOwner(int position);

    /**
     * Returns a fine to pay in order to get out of jail.
     *
     * @return amount to pay.
     */
    int getJailFine();

    /**
     * returns the list of properties owned by the player.
     *
     * @param player the player.
     * @return the list of properties.
     */
    List<IndexedEntry<Property>> getProperties(Player player);

    /**
     * Returns the starting position.
     *
     * @return the starting position.
     */
    int getStartPosition();


    /**
     * Returns the list of all properties in the game.
     *
     * @return the list of properties.
     */
    List<IndexedEntry<Property>> getAllProperties();

    /**
     * Returns the list of all non-owned properties.
     *
     * @return the list of properties.
     */
    List<IndexedEntry<Property>> getFreeProperties();

    /**
     * Withdraws the given value of coins from a player.
     *
     * @param player the player.
     * @param value  the value of coins to withdraw.
     * @throws BankException if the player doesn't have enough coins.
     */
    void withdraw(Player player, int value) throws BankException;

    /**
     * Returns the list of all players in the game.
     *
     * @return the list of players.
     */
    List<Player> getPlayers();

    /**
     * ends the current turn
     *
     * @param turn the turn to end
     * @throws GameException if the turn is not the current turn
     */
    void endTurn(Turn turn) throws GameException;

    /**
     * holds the current turn
     *
     * @param turn the turn to hold
     * @throws GameException if the turn is not the current turn
     */
    void holdTurn(BaseTurnImpl turn) throws GameException;

}
