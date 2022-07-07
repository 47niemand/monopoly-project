package pp.muza.monopoly.model.game;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.cards.Chance;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.List;

public interface Turn extends TurnPlayer {

    int nextPosition(int distance);

    Land getLand(int position);

    Player getPropertyOwner(int position);

    Chance popChanceCard();

    void setPlayerInJail();

    PlayerStatus getStatus();

    BigDecimal getJailFine();

    Player getPlayer();

    List<Land> moveTo(int position);

    void addMoney(BigDecimal amount) throws BankException;

    List<Land.Entry<Property>> getProperties();

    void buyProperty(int landId, Property property) throws BankException, TurnException;

    void payRent(Player recipient, BigDecimal amount) throws BankException;

    void payTax(BigDecimal amount) throws BankException;

    void leaveJail() throws TurnException;

    void endTurn() throws TurnException;

    void doContract(int landId, Property property, BigDecimal amount) throws BankException, TurnException;

    void ownProperty(int landId, Property property) throws TurnException;

    int getStartPos();

    int foundLandByName(String name);

    List<Integer> foundLandsByColor(Property.Color color);

    List<Player> getPlayers();

    void sendCard(Player player, ActionCard actionCard);

    List<Land.Entry<Property>> getFreeProperties();

    List<Land.Entry<Property>> getAllProperties();

    void createBirthday();

    PlayerStatus getPlayerStatus(Player player);
}
