package pp.muza.monopoly.model.game;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.Chance;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This interface represents a turn in the game.
 * There are methods to change the player status, move to position, to get the
 * land, to get the owner of a property etc.
 */
public interface Turn extends TurnPlayer {

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
     * Gets a chance card from the top of the deck.
     *
     * @return the chance card.
     */
    Chance popChanceCard();

    /**
     * set player to jail.
     */
    void setPlayerInJail();

    /**
     * get player's status.
     *
     * @return return status.
     */
    PlayerStatus getStatus();

    /**
     * returns a fine to pay in order to get out of jail.
     *
     * @return amount to pay.
     */
    BigDecimal getJailFine();

    /**
     * returns current player.
     *
     * @return player.
     */
    Player getPlayer();

    /**
     * Moves the player to the given position.
     *
     * @param position the position.
     * @return the path from the current postion (excluded) to the new position (included).
     */
    List<Land> moveTo(int position);

    /**
     * Adds the given amount to the player's balance.
     *
     * @param amount the amount to add.
     * @throws BankException if operation fails.
     */
    void addMoney(BigDecimal amount) throws BankException;

    /**
     * Returns the properties of the player.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getProperties();

    /**
     * Buys the property at the given position.
     *
     * @param landId   the land id.
     * @param property the property.
     * @throws BankException if player doesn't have enough money.
     * @throws TurnException if operation fails.
     */
    void buyProperty(int landId, Property property) throws BankException, TurnException;

    /**
     * Pays rent
     *
     * @param recipient the recipient.
     * @param amount    the amount to pay.
     * @throws BankException if operation fails.
     */
    void payRent(Player recipient, BigDecimal amount) throws BankException;

    /**
     * Pays tax.
     *
     * @param amount the amount to pay.
     * @throws BankException if operation fails.
     */
    void payTax(BigDecimal amount) throws BankException;

    /**
     * player leaves jail.
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
     * @param landId   the land id.
     * @param property the property of the land.
     * @param amount   the amount to pay.
     * @throws BankException if operation fails.
     * @throws TurnException if property do not belong to the player.
     */
    void doContract(int landId, Property property, BigDecimal amount) throws BankException, TurnException;

    /**
     * return the game's start postion
     *
     * @return the start position.
     */
    int getStartPos();

    /**
     * returns the postion of land by the given name.
     *
     * @param name the name of the land.
     * @return the position of the land.
     * @throws NoSuchElementException if land name does not exist.
     */
    int foundLandByName(String name);

    /**
     * returns positions of lands by the given color
     *
     * @param color the color of the land.
     * @return the positions of the lands, if there is no land with the given color, returns an empty list.
     */
    List<Integer> foundLandsByColor(Property.Color color);

    /**
     * returns all players in the game.
     *
     * @return the players.
     */
    List<Player> getPlayers();

    /**
     * send a card to the player.
     *
     * @param player     the player.
     * @param actionCard the to send.
     */
    void sendCard(Player player, ActionCard actionCard);

    /**
     * reruns not owned properties on the board.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getFreeProperties();

    /**
     * returns all properties on the board.
     *
     * @return the properties.
     */
    List<IndexedEntry<Property>> getAllProperties();

    /**
     * Birthday party.
     */
    void birthdayParty();

    /**
     * returns the status of the player.
     *
     * @param player the player.
     * @return the status.
     */
    PlayerStatus getPlayerStatus(Player player);

    /**
     * trades the property.
     *
     * @param salePlayer the player who is selling.
     * @param landId     the land id to sell.
     * @param property   the property to sell.
     * @throws BankException if player doesn't have enough money.
     * @throws TurnException operation fails if the salePlayer does not own the property.
     */
    void tradeProperty(Player salePlayer, int landId, Property property) throws BankException, TurnException;

    /**
     * removes start_turn/roll_dice actions from the player's stack.
     */
    void playerStartedTurn();
}
