package pp.muza.monopoly.model.game;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.Chance;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;

import java.math.BigDecimal;
import java.util.List;

/**
 * This is game API, which is used to play a turn.
 */
public interface GameTurn {

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
     * Returns the list of active action cards, which are available for the player at the moment.
     *
     * @param player the player.
     * @return the list of active action cards.
     */
    List<ActionCard> getActiveActionCards(Player player);

    /**
     * retrun the postion with the given distance from the current player's position.
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
    Chance popChanceCard();

    /**
     * Sets the player to jail.
     *
     * @param player the player.
     */
    void setPlayerInJail(Player player);

    /**
     * Returns status of the player.
     *
     * @param player the player.
     * @return true if the player is in jail, false otherwise.
     */
    PlayerStatus getPlayerStatus(Player player);

    /**
     * Birthday for the player. players who are in game have to pay a birthday fee.
     *
     * @param player the player.
     */
    void birthdayParty(Player player);

    //TODO: add java doc
    void tradeProperty(Player player, Player salePlayer, int landId) throws TurnException, BankException;

    void playerTurnStarted(Player player);

    BigDecimal getJailFine();

    int getPosition(Player player);

    List<Land> moveTo(Player player, int position);

    void addMoney(Player player, BigDecimal amount) throws BankException;

    List<IndexedEntry<Property>> getProperties(Player player);

    void buyProperty(Player player, int landId) throws TurnException, BankException;

    void payRent(Player player, int landId) throws TurnException, BankException;

    void pay(Player player, Player recipient, BigDecimal amount) throws BankException;

    void payTax(Player player, BigDecimal amount) throws BankException;

    void leaveJail(Player player) throws TurnException;

    void endTurn(Player player);

    void doContract(Player player, int landId, BigDecimal amount) throws TurnException, BankException;

    int getStartPosition();

    int findLandByName(String name);

    List<Integer> findLandsByColor(Property.Color color);

    List<Player> getPlayers();

    void sendCard(Player player, ActionCard actionCard);

    List<IndexedEntry<Property>> getAllProperties();
}
