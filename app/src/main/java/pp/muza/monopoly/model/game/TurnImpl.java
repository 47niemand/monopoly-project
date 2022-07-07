package pp.muza.monopoly.model.game;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardExecute;
import pp.muza.monopoly.model.actions.cards.Chance;
import pp.muza.monopoly.model.actions.cards.PayGift;
import pp.muza.monopoly.model.lands.Jail;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The turn of the game.
 * <p>
 * The turn is composed of a list of action cards.
 * There are different action cards (i.e. "Pay Rent", "New Turn", "Buy Land",
 * ...)
 * Players can execute action cards (actionCard.execute()).
 * Each action card has a boolean indicating if it is a mandatory: the ones that
 * have to be used during the turn (typeOfCard!=OPTIONAL) and the ones that are
 * not.
 * By executing an action card, the player can move to the next land or buy a
 * property or pay rent or pay tax etc. Or, player can get new action(s) card.
 * Actions can be executed in a priority order (the lowest first).
 * getActiveActionCards returns the list of action cards that can be executed at
 * the current moment.
 * The turn can be considered as finished when there are no more mandatory
 * action cards to execute.
 * </p>
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
        return game.getActiveActionCards(player);
    }

    @Override
    public boolean executeActionCard(ActionCard actionCard) throws TurnException {
        boolean result = game.executeActionCards(this, actionCard);
        LOG.info("Action card {} executed: {}", actionCard.getName(), result);
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
        game.addMoney(player, amount);
    }

    @Override
    public List<Land.Entry<Property>> getProperties() {
        List<Land> lands = game.getLands();
        List<Land.Entry<Property>> result = new ArrayList<>();
        for (int i = 0; i < lands.size(); i++) {
            Land land = lands.get(i);
            if (land.getType() == Land.Type.PROPERTY && game.getPropertyOwner(i) == player) {
                Property property = (Property) land;
                result.add(new Land.Entry<>(i, property));
            }
        }
        return result;
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
        game.addMoney(recipient, amount);
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
        finished = true;
        List<ActionCard> playerCards = game.getPlayerCards(player);
        LOG.info("Not used cards: {}",
                playerCards.stream().map(ActionCard::getName).collect(Collectors.joining(", ")));
        LOG.info("Used cards: {}",
                usedCards.stream().map(ActionCard::getName).collect(Collectors.joining(", ")));

        List<ActionCard> mandatoryCards = playerCards.stream()
                .filter(actionCard -> actionCard.getType().isMandatory())
                .collect(Collectors.toList());

        if (mandatoryCards.size() > 0) {
            // ask player to choose action card
            LOG.info("Player {} has mandatory cards: {}", player, mandatoryCards);
            game.setPlayerStatus(player, PlayerStatus.OUT_OF_GAME);
            // return properties to game
            getProperties().forEach(
                    x -> game.propertyOwnerRemove(x.getPosition())
            );
            playerCards.stream()
                    .filter(x -> x.getAction() == ActionCard.Action.CHANCE)
                    .forEach(x1 -> game.returnChanceCard((Chance) x1));
        }
    }

    @Override
    public void doContract(int landId, Property property, BigDecimal amount) throws BankException, TurnException {
        if (game.getPropertyOwner(landId) != player) {
            throw new TurnException("Land is not owned by you");
        }
        game.addMoney(player, amount);
        game.propertyOwnerRemove(landId);
    }

    @Override
    public void ownProperty(int landId, Property property) {
        game.setPropertyOwner(landId, player);
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
    public List<Land.Entry<Property>> getFreeProperties() {
        return getAllProperties().stream()
                .filter(x -> game.getPropertyOwner(x.getPosition()) == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Land.Entry<Property>> getAllProperties() {
        List<Land> l = game.getLands();
        List<Land.Entry<Property>> p = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            Land land = l.get(i);
            if (land.getType() == Land.Type.PROPERTY) {
                Property property = (Property) land;
                p.add(new Land.Entry<>(i, property));
            }
        }
        return p;
    }

    @Override
    public void createBirthday() {
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
}
