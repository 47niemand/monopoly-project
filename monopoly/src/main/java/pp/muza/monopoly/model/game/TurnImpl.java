package pp.muza.monopoly.model.game;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.Chance;
import pp.muza.monopoly.model.actions.PayGift;
import pp.muza.monopoly.model.lands.Jail;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * The turn of the game implementation.  This class implements the Turn and TurnPlayer interfaces.
 */

@Data
class TurnImpl implements Turn, TurnPlayer {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private final Game game;
    private final Player player;
    private final List<ActionCard> usedCards = new ArrayList<>();
    private boolean finished;

    @Override
    public List<ActionCard> getActiveActionCards() {
        List<ActionCard> result = game.getActiveActionCards(player);
        LOG.info("{}: active action cards: {}", player.getName(),
                result.stream().map(ActionCard::getName).collect(Collectors.toList()));
        return result;
    }

    @Override
    public boolean playCard(ActionCard actionCard) throws TurnException {
        if (isFinished()) {
            throw new TurnException("The turn is finished.");
        }
        LOG.info("{}: playing card {}", player.getName(), actionCard.getName());
        boolean result = game.playCard(this, actionCard);
        LOG.debug("Card {} played: {}", actionCard, result);
        usedCards.remove(actionCard);
        usedCards.add(actionCard);
        return result;
    }

    @Override
    public int nextPosition(int distance) {
        return game.getNextPosition(player, distance);
    }

    @Override
    public Land getLand(int position) {
        return game.getLand(position);
    }

    @Override
    public Player getPropertyOwner(int position) {
        return game.getPropertyOwner(position);
    }

    @Override
    public Chance popChanceCard() {
        return game.popChanceCard();
    }

    @Override
    public void setPlayerInJail() {
        game.setPlayerStatus(player, PlayerStatus.IN_JAIL);
    }

    @Override
    public PlayerStatus getStatus() {
        return game.getPlayerStatus(player);
    }

    @Override
    public BigDecimal getJailFine() {
        return game.getLands().stream()
                .filter(land -> land.getType() == Land.Type.JAIL)
                .map(x -> ((Jail) x).getFine())
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public List<Land> moveTo(int position) {
        List<Integer> path = game.getPathTo(game.getPlayerPosition(player), position);
        List<Land> lands = game.getLands(path);
        game.setPlayerPosition(player, position);
        return lands;
    }

    @Override
    public void addMoney(BigDecimal amount) throws BankException {
        game.deposit(player, amount);
    }

    @Override
    public List<IndexedEntry<Property>> getProperties() {
        return game.getProperties(player);
    }

    @Override
    public void buyProperty(int landId, Property property) throws BankException, TurnException {
        if (game.getPropertyOwner(landId) != null) {
            throw new TurnException("Land is already owned");
        }
        game.withdraw(player, property.getPrice());
        game.setPropertyOwner(landId, player);
    }

    @Override
    public void payRent(Player recipient, BigDecimal amount) throws BankException {
        game.withdraw(player, amount);
        game.deposit(recipient, amount);
    }

    @Override
    public void payTax(BigDecimal amount) throws BankException {
        game.withdraw(player, amount);
    }

    @Override
    public void leaveJail() throws TurnException {
        if (getStatus() != PlayerStatus.IN_JAIL) {
            throw new TurnException("Player is not in jail");
        }
        game.setPlayerStatus(player, PlayerStatus.IN_GAME);
    }

    @Override
    public void endTurn() throws TurnException {
        if (finished) {
            LOG.warn("Turn already finished");
            throw new TurnException("Turn already finished");
        }
        LOG.info("Finishing turn for player {}", player.getName());
        LOG.info("Used cards: {}",
                usedCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        game.endTurn(player);
        finished = true;
    }

    @Override
    public void doContract(int landId, Property property, BigDecimal amount) throws BankException, TurnException {
        if (game.getPropertyOwner(landId) != player) {
            throw new TurnException("Land is not owned by you");
        }
        assert game.getLand(landId) == property;
        game.deposit(player, amount);
        game.propertyOwnerRemove(landId);
    }

    @Override
    public int getStartPos() {
        return game.getStartPosition();
    }

    @Override
    public int foundLandByName(String name) {
        for (int i = 0; i < game.getLands().size(); i++) {
            if (game.getLands().get(i).getName().equals(name)) {
                return i;
            }
        }
        throw new NoSuchElementException("No land found with name " + name);
    }

    @Override
    public List<Integer> foundLandsByColor(Property.Color color) {
        List<Integer> lands = new ArrayList<>();
        for (int i = 0; i < game.getLands().size(); i++) {
            Land land = game.getLands().get(i);
            if (land instanceof Property) {
                Property property = (Property) land;
                if (property.getColor() == color) {
                    lands.add(i);
                }
            }
        }
        return lands;
    }

    @Override
    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    @Override
    public void sendCard(Player player, ActionCard actionCard) {
        game.sendCardToPlayer(player, actionCard);
    }

    @Override
    public List<IndexedEntry<Property>> getFreeProperties() {
        return getAllProperties().stream()
                .filter(x -> game.getPropertyOwner(x.getIndex()) == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        List<Land> l = game.getLands();
        List<IndexedEntry<Property>> p = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            Land land = l.get(i);
            if (land.getType() == Land.Type.PROPERTY) {
                Property property = (Property) land;
                p.add(new IndexedEntry<>(i, property));
            }
        }
        return p;
    }

    @Override
    public void birthdayParty() {
        game.getPlayers().stream()
                .filter(x -> x != player && !game.getPlayerStatus(x).isFinal())
                .forEach(x -> {
                    Turn subTurn = new TurnImpl(game, x);
                    game.sendCardToPlayer(x, PayGift.of(this.player, BigDecimal.valueOf(1)));
                    game.playTurn(subTurn);
                });
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return game.getPlayerStatus(player);
    }

    @Override
    public void tradeProperty(Player salePlayer, int landId, Property property) throws BankException, TurnException {
        if (game.getPropertyOwner(landId) != salePlayer) {
            throw new TurnException("Land is not owned by you");
        }
        BigDecimal price = property.getPrice();
        game.withdraw(player, price);
        game.deposit(salePlayer, price);
        game.setPropertyOwner(landId, player);
    }

    @Override
    public void playerStartedTurn() {
        // there is no need to roll dice or move if player did something in this turn
        game.playerTurnStarted(player);
    }

    @Override
    public String toString() {
        return "TurnImpl(game=" + this.getGame()
                + ", player=" + this.getPlayer().getName()
                + ", usedCards=" + this.getUsedCards().stream().map(ActionCard::getName).collect(Collectors.toList())
                + ", finished=" + this.isFinished() + ")";
    }
}
