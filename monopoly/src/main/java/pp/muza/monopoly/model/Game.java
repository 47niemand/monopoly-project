package pp.muza.monopoly.model;


import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.pieces.lands.PropertyColor;

/**
 * This is game API, which is used to play a turn.
 */
public interface Game {

    /**
     * Returns the current game information.
     *
     * @return game info snapshot.
     */
    GameInfo getGameInfo();

    /**
     * Play a card.
     *
     * @param turn       the turn.
     * @param actionCard the card to play.
     * @return true if the card was played, false otherwise.
     * @throws TurnException if there are some errors.
     */
    boolean playCard(Turn turn, ActionCard actionCard) throws TurnException;

    /**
     * Returns the list of active action cards, which are available for the player
     * at the moment.
     *
     * @param player the player.
     * @return the list of active action cards.
     */
    List<ActionCard> getActiveActionCards(Player player);

    /**
     * return the position with the given distance from the current player's
     * position.
     *
     * @param player   the player.
     * @param distance the distance to move.
     * @return the new position.
     */
    int getNextPosition(Player player, int distance);

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
     * Pops a chance card from the top of the deck.
     *
     * @return the chance card.
     */
    Fortune popFortuneCard();

    /**
     * Sets a player to jail.
     *
     * @param player the player.
     */
    void setPlayerInJail(Player player);

    /**
     * Returns status of a player.
     *
     * @param player the player.
     * @return true if the player is in jail, false otherwise.
     */
    PlayerStatus getStatus(Player player);

    /**
     * Birthday for a player. Players who are in game have to pay a birthday fee.
     *
     * @param player the player.
     */
    void birthdayParty(Player player);

    /**
     * Buy a property between two players.
     *
     * @param player     the player who is buying the property.
     * @param salePlayer the player who is selling the property.
     * @param landId     the property to trade.
     * @throws TurnException if there are some errors (e.g. the landId is not owned
     *                       by the sale player).
     * @throws BankException if operation fails (e.g. if the player doesn't have
     *                       enough coins).
     */
    void tradeProperty(Player player, Player salePlayer, int landId) throws TurnException, BankException;

    /**
     * Removes cards NEW_TURN and ROLL_DICE from the player's hand.
     * there is no need to roll dice or move if a player did something in this turn.
     *
     * @param player the player.
     */
    void playerTurnStarted(Player player);

    /**
     * Returns a fine to pay in order to get out of jail.
     *
     * @return number to pay.
     */
    int getJailFine();

    /**
     * Returns the position of the player.
     *
     * @param player the player.
     * @return the position.
     */
    int getPosition(Player player);

    /**
     * Moves the player to the given position and returns the path player was moved on.
     *
     * @param player   the player.
     * @param position the position.
     * @return the path from the current position (excluded) to the new position
     * (included).
     */
    List<Land> moveTo(Player player, int position);

    /**
     * returns the list of properties owned by the player.
     *
     * @param player the player.
     * @return the list of properties.
     */
    List<IndexedEntry<Property>> getProperties(Player player);

    /**
     * Buys the property at the given position for the player.
     *
     * @param player the player.
     * @param landId the property to buy.
     * @throws TurnException if there are some errors (e.g. someone already owns the
     *                       landId).
     * @throws BankException if the player doesn't have enough coins.
     */
    void buyProperty(Player player, int landId) throws TurnException, BankException;


    /**
     * Get the number of coins needed to cover the rent.
     *
     * @param landId the property.
     * @return the number of coins to pay for the rent, or 0 if anyone does not own the property.
     */
    int getRent(int landId);

    /**
     * transfers the given number of coins from the player to the recipient.
     *
     * @param player    the player who is sending the coins.
     * @param recipient the player who is receiving the coins.
     * @param number    the number of coins to transfer.
     * @throws BankException if the player doesn't have enough coins.
     */
    void pay(Player player, Player recipient, int number) throws BankException;


    /**
     * Leaves the jail.
     *
     * @param player the player who is leaving the jail.
     * @throws TurnException if the player is not in jail.
     */
    void leaveJail(Player player) throws TurnException;

    /**
     * Ends the turn.
     * If a player has an obligation at the end of the turn, the player will lose the game.
     *
     * @param player the player.
     */
    void endTurn(Player player);

    /**
     * Sales a property at the given position to the bank.
     *
     * @param player the player.
     * @param landId the property.
     * @throws TurnException if there are some errors (e.g. the landId is not owned
     *                       by the player).
     * @throws BankException if a player cannot receive the coins.
     */
    void doContract(Player player, int landId) throws TurnException, BankException;

    /**
     * Returns the starting position.
     *
     * @return the starting position.
     */
    int getStartPosition();

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
     * Returns the list of all players.
     *
     * @return the list of players.
     */
    List<Player> getPlayers();

    /**
     * Set a player position
     *
     * @param player   the player
     * @param position the position
     */
    void setPosition(Player player, int position);

    /**
     * Sends the action card to the player.
     *
     * @param player     the player.
     * @param actionCard the action card.
     */
    void sendCard(Player player, ActionCard actionCard);

    /**
     * Returns the list of all properties in the game.
     *
     * @return the list of properties.
     */
    List<IndexedEntry<Property>> getAllProperties();

    /**
     * Returns the list of all owned properties.
     *
     * @return the list of all owned properties.
     */
    Map<Integer, Player> getPropertyOwners();

    /**
     * Adds coins to the player's wallet.
     *
     * @param player the player.
     * @param value the value of coins to add.
     * @throws BankException if operation fails (e.g. if the player wallet is full).
     */
    void income(Player player, int value) throws BankException;

    /**
     * Withdraws the given value of coins from a player.
     *
     * @param player the player.
     * @param value the value of coins to withdraw.
     * @throws BankException if the player doesn't have enough coins.
     */
    void withdraw(Player player, int value) throws BankException;
}
