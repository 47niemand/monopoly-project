package pp.muza.monopoly.model.turn;

import java.math.BigDecimal;
import java.util.List;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.cards.chance.Chance;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

public interface Turn extends TurnPlayer {

    /**
     * Add an action card to the turn.
     * There only can be one action card of the same type.
     *
     * @param actionCard the action card to add to the turn
     * @return true if the action card was added, false otherwise
     */
    boolean addActionCard(ActionCard actionCard);

    /**
     * Remove the action card from the player's hand if it was used.
     *
     * @param actionCard the action card to remove
     */
    void removeCardsWhenUsed(ActionCard actionCard);

    void setPlayerInJail();

    void endTurn();

    void payTax(BigDecimal amount) throws BankException;

    void leaveJail();

    int rollDice();

    void payRent(Player recipient, BigDecimal amount) throws BankException;

    BigDecimal getJailFine();

    int getDestination(int steps);

    List<Land> moveTo(int endPos);

    Chance popChanceCard();

    void addMoney(BigDecimal amount) throws BankException;

    void doContract(int landId, Property property, BigDecimal amount) throws BankException;

    void buyProperty(int landId, Property property) throws BankException, TurnException;

    int getStartPos();

    int foundLandByName(String name);

    List<Integer> foundLandsByColor(Property.Color color);

    List<Land.Entry<Property>> getFreeProperties();

    List<Land.Entry<Property>> getAllProperties();

    void sendGiftCard(Player player, ActionCard giftCard);

    void ownProperty(int landId, Property property);

    boolean isPlayerInGame(Player player);

    Land getLand(int landId);
}
