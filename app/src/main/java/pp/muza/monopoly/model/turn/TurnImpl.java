package pp.muza.monopoly.model.turn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.actions.cards.NewTurn;
import pp.muza.monopoly.model.actions.cards.chance.Chance;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.lands.Jail;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

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

public class TurnImpl implements Turn, TurnPlayer {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private final Game game;
    private final Player player;
    private final List<ActionCard> actionCards = new ArrayList<>();
    private final List<ActionCard> usedActionCards = new ArrayList<>();
    private boolean isFinished;

    public TurnImpl(Game game, Player player) {
        LOG.info("New turn for player {}", player.getName());
        this.game = game;
        this.player = player;
        this.addActionCard(NewTurn.of());
        game.getPlayerChanceCards(player).forEach(this::addActionCard);
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public boolean addActionCard(ActionCard actionCard) {
        boolean result = false;
        LOG.info("Adding action card {} to player {}", actionCard, player.getName());
        // add the action card to the player's hand if it is not already in the player's
        if (!this.actionCards.contains(actionCard)) {
            this.actionCards.add(actionCard);
            result = true;
        } else {
            LOG.warn("Action card {} already in the player's hand", actionCard.getName());
        }
        return result;
    }

    @Override
    public List<ActionCard> getActiveActionCards() {
        List<ActionCard> result;
        if (isFinished) {
            LOG.warn("Turn is finished, no more action cards can be executed");
            result = ImmutableList.of();
        } else {
            int currentPriority = actionCards.stream()
                    .filter(actionCard -> actionCard.getType().isMandatory())
                    .mapToInt(ActionCard::getPriority)
                    .min().orElse(0);
            LOG.info("Current priority: {}", currentPriority);
            result = actionCards.stream().filter(actionCard -> actionCard.getPriority() <= currentPriority)
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public void removeCardsWhenUsed(ActionCard actionCard) {
        LOG.info("Mark action card {} as used", actionCard.getName());
        if (actionCards.remove(actionCard)) {
            usedActionCards.add(actionCard);
            if (actionCard.getType() == ActionCard.Type.CHANCE) {
                // remove all chance cards from the actionCards with same priority
                List<ActionCard> restOfChanceCards = actionCards.stream()
                        .filter(x -> x.getType() == ActionCard.Type.CHANCE
                                && x.getPriority() == actionCard.getPriority())
                        .collect(Collectors.toList());
                restOfChanceCards.forEach(x -> {
                    if (!actionCards.remove(x)) {
                        throw new IllegalStateException("Action card " + x.getName() + " not found in actionCards");
                    }
                    usedActionCards.add(x);
                });
            }
        } else {
            LOG.warn("Action card {} not in the player's hand", actionCard.getName());
            throw new IllegalArgumentException("Action card not found");
        }
    }

    @Override
    public void setPlayerInJail() {
        game.setPlayerPos(player, game.getJailPos());
        game.setPlayerStatus(player, Game.PlayerStatus.IN_JAIL);
    }

    @Override
    public void endTurn() {
        if (isFinished) {
            LOG.warn("Turn already finished");
            throw new IllegalStateException("Turn already finished");
        }
        // log action cards
        LOG.info("Action cards: {}",
                actionCards.stream().map(ActionCard::getName).collect(Collectors.joining(", ")));
        // log used action cards
        LOG.info("Used action cards: {}",
                usedActionCards.stream().map(ActionCard::getName).collect(Collectors.joining(", ")));
        // return unused keppable action cards to the player's hand
        actionCards.stream()
                .filter(x -> x.getAction() == ActionCard.Action.CHANCE && x.getType() == ActionCard.Type.KEEPABLE)
                .forEach(x -> game.returnChanceCardToPlayer(player, x));
        // return chance cards to the pile
        Stream.concat(usedActionCards.stream(), actionCards.stream())
                .filter(x -> x.getAction() == ActionCard.Action.CHANCE)
                .forEach(x1 -> game.returnChanceCard((Chance) x1));

        List<ActionCard> mandatoryActionCards = getActiveActionCards().stream()
                .filter(actionCard -> actionCard.getType().isMandatory()
                        && actionCard.getAction() != ActionCard.Action.END_TURN)
                .collect(Collectors.toList());

        if (mandatoryActionCards.size() > 0) {
            // ask player to choose action card
            LOG.info("Player {} has mandatory action cards: {}", player, mandatoryActionCards);
            game.setPlayerLost(player);
        }

        LOG.info("Turn finished");
        isFinished = true;

    }

    @Override
    public void payTax(BigDecimal amount) throws BankException {
        game.payTax(player, amount);
    }

    @Override
    public void leaveJail() {
        if (game.getPlayerStatus(player) == Game.PlayerStatus.IN_JAIL) {
            game.leaveJail(player);
        }
    }

    @Override
    public int rollDice() {
        return game.rollDice();
    }

    @Override
    public void payRent(Player recipient, BigDecimal amount) throws BankException {
        game.payRent(player, recipient, amount);
    }

    @Override
    public Game.PlayerStatus getStatus() {
        return game.getPlayerStatus(player);
    }

    @Override
    public BigDecimal getJailFine() {
        Jail jail = (Jail) game.getBoard().getLand(game.getJailPos());
        return jail.getFine();
    }

    @Override
    public int getDestination(int steps) {
        int startPos = game.getPlayerPos(player);
        return game.getBoard().getDestination(startPos, steps);
    }

    @Override
    public List<Land> moveTo(int endPos) {
        List<Land> result;
        int startPos = game.getPlayerPos(player);
        if (startPos == endPos) {
            LOG.warn("Start and end position are the same");
            result = ImmutableList.of();
        } else {
            List<Integer> path = game.getBoard().getPathTo(startPos, endPos);
            List<Land> lands = game.getBoard().getLands(path);
            game.setPlayerPos(player, endPos);
            result = lands;
        }
        return result;
    }

    @Override
    public Chance popChanceCard() {
        Chance result = game.popChanceCard();
        LOG.info("Pop chance card {}", result);
        return result;
    }

    @Override
    public Player getLandOwner(int landId) {
        return game.getLandOwner(landId);
    }

    @Override
    public void addMoney(BigDecimal amount) throws BankException {
        game.addMoney(player, amount);
    }

    @Override
    public List<Land.Entry<Property>> getProperties() {
        return game.getProperties(player);
    }

    @Override
    public void doContract(int landId, Property property, BigDecimal amount) throws BankException {
        game.doContract(player, landId, property, amount);
    }

    @Override
    public void buyProperty(int landId, Property property) throws BankException, TurnException {
        int playerPos = game.getPlayerPos(player);
        if (playerPos == landId) {
            game.buyProperty(player, landId, property);
        } else {
            throw new TurnException("Player is not on the property");
        }
    }

    @Override
    public int getStartPos() {
        return game.getStartPos();
    }

    @Override
    public int foundLandByName(String name) {
        return game.foundLandByName(name);
    }

    @Override
    public List<Integer> foundLandsByColor(Property.Color color) {
        return game.foundLandsByColor(color);
    }

    @Override
    public List<Land.Entry<Property>> getFreeProperties() {
        return getAllProperties().stream().filter(x -> game.isOwned(x.getPosition())).collect(Collectors.toList());
    }

    @Override
    public List<Land.Entry<Property>> getAllProperties() {
        List<Land> l = game.getBoard().getLands();
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
    public void sendGiftCard(Player player, ActionCard giftCard) {
        game.returnChanceCardToPlayer(player, giftCard);
    }

    @Override
    public void ownProperty(int landId, Property property) {
        game.ownProperty(player, landId, property);
    }

    @Override
    public boolean isPlayerInGame(Player player) {
        return !game.getPlayerStatus(player).isFinal();
    }

    @Override
    public Land getLand(int landId) {
        return game.getBoard().getLand(landId);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void executeActionCard(ActionCard actionCard) throws ActionCardException {
        if (!actionCards.contains(actionCard)) {
            LOG.error("Action card {} not in the player's hand", actionCard.getName());
            throw new IllegalArgumentException("Action card not found");
        }
        actionCard.execute(this);
    }

    @Override
    public List<Player> getPlayers() {
        return game.getPlayers();
    }
}
