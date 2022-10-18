package pp.muza.monopoly.model;

import java.util.List;
import java.util.NoSuchElementException;

import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;

/**
 * Turn API for the engine.
 * <p>There are methods to change the player status, move to position, to get the
 * land, to get the owner of a property etc.</p>
 *
 * @author dmytromuza
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
     * @param position the land id.
     * @throws BankException if a player doesn't have enough coins.
     * @throws TurnException if operation fails.
     */
    void buyProperty(int position) throws BankException, TurnException;

    /**
     * The player leaves jail.
     *
     * @throws TurnException if the player is not in jail.
     */
    void leaveJail() throws TurnException;

    /**
     * Executes the contract
     *
     * @param position the land id.
     * @param price    the price.
     * @throws BankException if the player doesn't have enough coins.
     * @throws TurnException if the player is not in the game.
     */
    void doContract(int position, int price) throws BankException, TurnException;

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
     * @throws TurnException if the player is not in the game.
     */
    void sendCard(Player to, ActionCard actionCard) throws TurnException;

    /**
     * Holds current turn and starts the auction.
     *
     * @param position the opening price and the land id.
     * @param price    the opening price.
     * @throws TurnException if it is not currently a player's turn.
     */
    void auction(int position, int price) throws TurnException;

    /**
     * trades a property.
     *
     * @param seller   the player who is selling.
     * @param position the land id to sell.
     * @throws BankException if a player doesn't have enough coins.
     * @throws TurnException if the player is not in the game.
     */
    void tradeProperty(Player seller, int position) throws BankException, TurnException;


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

    /**
     * Do bid.
     *
     * @param position the position.
     * @param price    the price.
     * @throws TurnException if operation fails (wrong position, wrong price, auction is not guaranteed to be available for sale on the market at the moment).
     */
    void doBid(int position, int price) throws TurnException;

    /**
     * Returns the balance of the player.
     *
     * @return the player's balance.
     */
    int getBalance();

    /**
     * Sale of a property to the buyer.
     * The buyer pays the seller the price.
     * Then the buyer receives the property.
     *
     * @param position the position of the property.
     * @param price    the price of the property.
     * @param buyer    the buyer.
     * @throws BankException if a buyer doesn't have enough coins.
     * @throws TurnException if operation fails.
     */
    void doSale(int position, int price, Player buyer) throws TurnException, BankException;


    /**
     * Ends the auction.
     *
     * @return the winner of the auction, or null if there is no winner.
     * @throws TurnException if the auction is not started, or the current player is not the auctioneer.
     */
    Biding endAuction() throws TurnException;

    /**
     * Holds current turn.
     * <p>
     * Other players should player their cards.
     * </p>
     *
     * @throws TurnException if the player cannot hold the turn.
     */
    void holdTurn() throws TurnException;
}
