package pp.muza.monopoly.model;

import java.util.List;
import java.util.NoSuchElementException;

import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;

/**
 * This interface represents a turn in the game.
 * There are methods to change the player status, move to position, to get the
 * land, to get the owner of a property etc.
 */
public interface Turn {

    /**
     * Gets a chance card from the top of the deck.
     *
     * @return the chance card.
     */
    Fortune popFortuneCard();

    /**
     * set player to jail.
     *
     * @throws TurnException if the player is not in the game.
     */
    void setPlayerInJail() throws TurnException;

    /**
     * Moves the player to the given position.
     *
     * @param position the position.
     * @return the path from the current position (excluded) to the new position
     * (included).
     * @throws TurnException if the player is not in the game.
     */
    List<Land> moveTo(int position) throws TurnException;


    /**
     * Buys a property at the given position.
     *
     * @param landId the land id.
     * @throws BankException if a player doesn't have enough coins.
     * @throws TurnException if operation fails.
     */
    void buyProperty(int landId) throws BankException, TurnException;

    /**
     * The player leaves jail.
     *
     * @throws TurnException if the player is not in jail.
     */
    void leaveJail() throws TurnException;

    /**
     * Executes the contract
     *
     * @param landId the land id.
     * @throws BankException if the player doesn't have enough coins.
     * @throws TurnException if the player is not in the game.
     */
    void doContract(int landId) throws BankException, TurnException;

    /**
     * returns the position of land by the given name.
     *
     * @param asset the name of the land.
     * @return the position of the land.
     * @throws NoSuchElementException if a land name does not exist.
     */
    int foundProperty(Asset asset);

    /**
     * returns positions of lands by the given color
     *
     * @param color the color of the land.
     * @return the position of the lands, if there is no land with the given color,
     * returns an empty list.
     */
    List<Integer> foundLandsByColor(PropertyColor color);

    /**
     * Sends a card to the player.
     *
     * @param to         the player.
     * @param actionCard the action card.
     */
    void sendCard(Player to, ActionCard actionCard) throws TurnException;

    /**
     * Birthday party.
     */
    void doBirthdayParty() throws TurnException;

    /**
     * Holds current turn.
     *
     * @throws TurnException
     */

    void holdTurn() throws TurnException;

    /**
     * trades a property.
     *
     * @param seller the player who is selling.
     * @param landId the land id to sell.
     * @throws BankException if a player doesn't have enough coins.
     * @throws TurnException if the player is not in the game.
     */
    void tradeProperty(Player seller, int landId) throws BankException, TurnException;


    /**
     * returns number of coins to pay for the rent.
     *
     * @param position the position of the land.
     * @return the number of coins to pay.
     */
    int getRent(int position);

    /**
     * adds number to the player's balance.
     *
     * @param value the number to add.
     * @throws BankException if operation fails.
     */
    void income(int value) throws BankException;


    /**
     * get player's status.
     *
     * @return return status.
     */
    PlayerStatus getPlayerStatus();


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
     * @return number to pay.
     */
    int getJailFine();

    /**
     * Returns the properties of the player.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getProperties();

    /**
     * return the game's start position
     *
     * @return the start position.
     */
    int getStartPos();

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

    /**
     * reruns list for unowned properties on the board.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getFreeProperties();

    /**
     * withdraws the value of coins from the player.
     *
     * @param value to withdraw
     * @throws BankException if a player doesn't have enough coins.
     */
    void withdraw(int value) throws BankException;

    /**
     * returns the turn's player.
     *
     * @return the player
     */
    Player getPlayer();

    /**
     * Finishes the turn.
     *
     * @throws TurnException if operation fails.
     */
    void endTurn() throws TurnException;

    /**
     * Returns the list of players.
     *
     * @return list of the players in the game.
     */
    List<Player> getPlayers();

}
