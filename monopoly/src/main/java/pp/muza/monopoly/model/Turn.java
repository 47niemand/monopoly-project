package pp.muza.monopoly.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
     */
    void setPlayerInJail();

    /**
     * Moves the player to the given position.
     *
     * @param position the position.
     * @return the path from the current postion (excluded) to the new position
     * (included).
     */
    List<Land> moveTo(int position);


    /**
     * Buys a property at the given position.
     *
     * @param landId the land id.
     * @throws BankException if player doesn't have enough money.
     * @throws TurnException if operation fails.
     */
    void buyProperty(int landId) throws BankException, TurnException;

    /**
     * The player pays a tax to the bank.
     *
     * @param amount the amount to pay.
     * @throws BankException if operation fails.
     */
    void payTax(BigDecimal amount) throws BankException;

    /**
     * The player leaves jail.
     *
     * @throws TurnException if the player is not in jail.
     */
    void leaveJail() throws TurnException;

    /**
     * Ends the turn.
     *
     * @throws TurnException if operation fails.
     */
    void endTurn() throws TurnException;

    /**
     * Executes the contract
     *
     * @param landId the land id.
     * @throws BankException if operation fails.
     * @throws TurnException if property does not belong to the player.
     */
    void doContract(int landId) throws BankException, TurnException;

    /**
     * returns the postion of land by the given name.
     *
     * @param asset the name of the land.
     * @return the position of the land.
     * @throws NoSuchElementException if a land name does not exist.
     */
    int foundProperty(Property.Asset asset);

    /**
     * returns positions of lands by the given color
     *
     * @param color the color of the land.
     * @return the position of the lands, if there is no land with the given color,
     * returns an empty list.
     */
    List<Integer> foundLandsByColor(Property.Color color);

    /**
     * send a card to the player.
     *
     * @param player     the player.
     * @param actionCard action card.
     */
    void sendCard(Player player, ActionCard actionCard);

    /**
     * Birthday party.
     */
    void birthdayParty();

    /**
     * trades a property.
     *
     * @param salePlayer the player who is selling.
     * @param landId     the land id to sell.
     * @throws BankException if player doesn't have enough money.
     * @throws TurnException operation fails if the salePlayer does not own the
     *                       property.
     */
    void tradeProperty(Player salePlayer, int landId) throws BankException, TurnException;

    /**
     * removes start_turn/roll_dice actions from the player's stack.
     */
    void playerTurnStarted();

    /**
     * returns amount of money to pay for the rent.
     *
     * @param position the position of the land.
     * @return the amount of money to pay.
     */
    BigDecimal getRent(int position);

    /**
     * adds amount to the player's balance.
     *
     * @param amount the amount to add.
     * @throws BankException if operation fails.
     */
    void income(BigDecimal amount) throws BankException;


    /**
     * Returns the property owners map.
     *
     * @return the property owners map.
     */
    Map<Integer, Player> getPropertyOwners();

    /**
     * Returns the active action cards for the player at the moment.
     *
     * @return the list of active action cards.
     */
    List<ActionCard> getActiveActionCards();

    /**
     * Executes an action card.
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

    /**
     * reruns list for unowned properties on the board.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getFreeProperties();

    /**
     * withdraws the amount of money from the player.
     *
     * @param amount to withdraw
     * @throws BankException if player doesn't have enough money.
     */
    void withdraw(BigDecimal amount) throws BankException;
}
