package pp.muza.monopoly.model;

import java.math.BigDecimal;
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
public interface Turn extends TurnPlayer {

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
     *         (included).
     */
    List<Land> moveTo(int position);

    /**
     * The Player crossed the start field.
     *
     * @throws BankException if operation fails.
     */
    void crossedStart() throws BankException;

    /**
     * Buys a property at the given position.
     *
     * @param landId the land id.
     * @throws BankException if player doesn't have enough money.
     * @throws TurnException if operation fails.
     */
    void buyProperty(int landId) throws BankException, TurnException;

    /**
     * The player pays the rent of the property at the given position.
     *
     * @param landId the land id.
     * @throws BankException if player doesn't have enough money.
     * @throws TurnException if operation fails (e.g. if the player doesn't own the
     *                       property).
     */
    void payRent(int landId) throws BankException, TurnException;

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
     * @throws TurnException if player is not in jail.
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
     *         returns an empty list.
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
     * pay the amount of money to the recipient.
     *
     * @param recipient the recipient.
     * @param amount    the amount to pay.
     * @throws BankException if operation fails (player doesn't have enough money).
     */
    void pay(Player recipient, BigDecimal amount) throws BankException;

    /**
     * reruns list for unowned properties on the board..
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getFreeProperties();
}
