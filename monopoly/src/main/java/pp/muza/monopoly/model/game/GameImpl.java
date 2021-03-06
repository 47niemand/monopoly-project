package pp.muza.monopoly.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Strategy;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;
import pp.muza.monopoly.model.pieces.actions.PayGift;
import pp.muza.monopoly.model.pieces.lands.Jail;
import pp.muza.monopoly.model.turn.TurnImpl;

/**
 * The game.
 * <p>
 * This class is responsible for managing the game.
 */

public class GameImpl extends BaseGame implements Game {

    static final Logger LOG = LoggerFactory.getLogger(GameImpl.class);
    static final int BIRTHDAY_GIFT_AMOUNT = 1;

    public GameImpl(Board board, List<Player> players, List<Fortune> fortunes, List<Strategy> strategies, Bank bank) {
        super(bank, fortunes, board);
        this.players.addAll(players);
        Iterator<Strategy> strategyIterator = strategies.iterator();
        Strategy strategy = null;
        for (Player player : players) {
            if (strategyIterator.hasNext()) {
                strategy = strategyIterator.next();
            }
            playerData.put(player,
                    new PlayerData(player, PlayerStatus.IN_GAME, getStartPosition(), strategy));
            this.bank.set(player, BigDecimal.valueOf(18));
        }
        assert players.size() == playerData.keySet().size();
        Collections.shuffle(this.fortuneCards);
        currentPlayerIndex = 0;
    }

    public GameImpl(GameInfo gameInfo, List<Strategy> strategies, Bank bank) {
        super(gameInfo, strategies, bank);
    }

    @Override
    public boolean playCard(Turn turn, ActionCard actionCard) throws TurnException {
        Player player = turn.getPlayer();
        boolean cardUsed;
        boolean newCardsSpawned;
        int priority = getCurrentPriority(player);
        if (playerData.get(player).getActionCards().removeIf(x -> x.getPriority() <= priority && x.equals(actionCard))) {
            List<ActionCard> result = ((BaseActionCard) actionCard).play(turn);
            cardUsed = !result.contains(actionCard);
            // create a collection that does not contain cards that are already on player's hand
            List<ActionCard> newCards = result.stream().filter(x -> {
                boolean found = playerData.get(player).getActionCards().contains(x);
                if (found) {
                    LOG.debug("Card {} already on player's hand", x);
                }
                return !found;
            }).collect(Collectors.toList());

            if (!cardUsed && actionCard.getType() == ActionCard.Type.KEEPABLE) {
                LOG.warn("Card {} is not used, but it is keepable", actionCard);
            }

            newCardsSpawned = (newCards.size() > 0 && cardUsed) || (newCards.size() > 1);
            LOG.info("{} used {} and spawned {} new cards{}", player.getName(),
                    actionCard.getName(),
                    newCardsSpawned ? "some" : "no",
                    (newCardsSpawned ? ": " + newCards.stream().map(ActionCard::getName).collect(Collectors.toList()) : ""));
            playerData.get(player).getActionCards().addAll(newCards);

            // Choose cards (ActionCard.Type.CHOOSE) can only be used once, thus we must take them out of the player's hand.
            if (actionCard.getType() == ActionCard.Type.CHOOSE) {
                List<ActionCard> chooses = playerData.get(player)
                        .getActionCards()
                        .stream()
                        .filter(x -> x.getType() == ActionCard.Type.CHOOSE
                                && x.getPriority() <= actionCard.getPriority())
                        .collect(Collectors.toList());
                playerData.get(player).getActionCards().removeAll(chooses);
                LOG.info("Removing choose cards from player's hand: {}", chooses.stream().map(ActionCard::getName).collect(Collectors.toList()));
            }

            // return chance card (ActionCard.Action.CHANCE) to the game
            if (cardUsed && (actionCard.getAction() == ActionCard.Action.CHANCE)) {
                LOG.debug("Returning chance card {} to the game", actionCard);
                getBackChanceCard(actionCard);
            }
        } else if (playerData.get(player).getActionCards().contains(actionCard)) {
            throw new TurnException(String.format("Player can't use %s at this time", actionCard.getName()));
        } else {
            throw new IllegalStateException("Action card not found");
        }

        return cardUsed || newCardsSpawned;
    }


    @Override
    Turn turn(Player player) {
        return new TurnImpl(this, player);
    }

    @Override
    public PlayerStatus getStatus(Player player) {
        return playerData.get(player).getStatus();
    }

    @Override
    public void birthdayParty(Player player) {
        players.stream()
                .filter(x -> x != player && !getStatus(x).isFinished())
                .forEach(x -> {
                    Turn subTurn = turn(x);
                    sendCard(x, PayGift.of(player, BigDecimal.valueOf(BIRTHDAY_GIFT_AMOUNT)));
                    playTurn(subTurn);
                });
    }

    @Override
    public void tradeProperty(Player player, Player salePlayer, int landId) throws BankException, TurnException {
        Property property = (Property) board.getLand(landId);
        if (getPropertyOwner(landId) != salePlayer) {
            throw new TurnException("Land is not owned by " + salePlayer.getName());
        }
        if (player == salePlayer) {
            throw new TurnException("You can't trade with yourself");
        }
        BigDecimal price = property.getPrice();
        bank.withdraw(player, price);
        bank.deposit(salePlayer, price);
        setPropertyOwner(landId, player);
    }

    @Override
    public int getNextPosition(Player player, int distance) {
        return board.getDestination(playerData.get(player).getPosition(), distance);
    }

    @Override
    public int getPosition(Player player) {
        return playerData.get(player).getPosition();
    }

    @Override
    public List<Land> moveTo(Player player, int position) {
        List<Integer> path = board.getPathTo(getPosition(player), position);
        List<Land> lands = board.getLands(path);
        playerData.get(player).setPosition(position);
        return lands;
    }

    @Override
    public void sendCard(Player player, ActionCard actionCard) {
        LOG.info("Player {} get {} card", player.getName(), actionCard.getName());
        playerData.get(player).getActionCards().add(actionCard);
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        List<Land> l = board.getLands();
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
    public Map<Integer, Player> getPropertyOwners() {
        return ImmutableMap.copyOf(propertyOwners);
    }

    @Override
    public void income(Player player, BigDecimal amount) throws BankException {
        bank.deposit(player, amount);
    }

    @Override
    public void withdraw(Player player, BigDecimal amount) throws BankException {
        bank.withdraw(player, amount);
    }

    @Override
    public GameInfo getGameInfo() {
        List<Player> players = ImmutableList.copyOf(getPlayers());
        ImmutableList.Builder<PlayerInfo> playerInfoBuilder = ImmutableList.builder();
        for (Player player : players) {
            playerInfoBuilder.add(getPlayerInfo(player));
        }
        return new GameInfo(players, playerInfoBuilder.build(), board, fortuneCards, currentPlayerIndex, turnNumber, maxTurns);
    }

    @Override
    public List<ActionCard> getActiveActionCards(Player player) {
        List<ActionCard> result;
        List<ActionCard> actionCards = playerData.get(player).getActionCards();

        int currentPriority = getCurrentPriority(player);

        LOG.debug("Player's {} current priority: {}", player.getName(), currentPriority);
        result = actionCards.stream()
                .filter(actionCard -> actionCard.getPriority() <= currentPriority)
                .sorted(Comparator.comparing(ActionCard::getPriority))
                .collect(Collectors.toList());
        LOG.debug("Player's {} active action cards: {}", player.getName(), result);

        return result;
    }

    @Override
    public Player getPropertyOwner(int landId) {
        return propertyOwners.get(landId);
    }

    @Override
    public int getStartPosition() {
        return board.getStartPosition();
    }

    @Override
    public int findProperty(Property.Asset asset) {
        for (int i = 0; i < board.getLands().size(); i++) {
            Land land = board.getLands().get(i);
            if (land.getType() == Land.Type.PROPERTY) {
                assert land instanceof Property;
                Property property = (Property) land;
                if (property.getAsset() == asset) {
                    return i;
                }
            }
        }
        throw new NoSuchElementException("No land found with name " + asset);
    }

    @Override
    public List<Integer> findLandsByColor(Property.Color color) {
        List<Integer> lands = new ArrayList<>();
        for (int i = 0; i < board.getLands().size(); i++) {
            Land land = board.getLands().get(i);
            if (land.getType() == Land.Type.PROPERTY) {
                assert land instanceof Property;
                Property property = (Property) land;
                if (property.getColor() == color) {
                    lands.add(i);
                }
            }
        }
        return lands;
    }

    @Override
    public Land getLand(int position) {
        return board.getLand(position);
    }

    @Override
    public Fortune popFortuneCard() {
        return fortuneCards.pop();
    }

    @Override
    public void setPlayerInJail(Player player) {
        playerData.get(player).setStatus(PlayerStatus.IN_JAIL);
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public void endTurn(Player player) {
        List<ActionCard> playerCards = playerData.get(player).getActionCards();
        LOG.info("Not used cards: {}",
                playerCards.stream().map(ActionCard::getName).collect(Collectors.toList()));

        List<ActionCard> mandatoryCards = playerCards.stream()
                .filter(actionCard -> actionCard.getType().isMandatory())
                .collect(Collectors.toList());

        if (mandatoryCards.size() > 0) {
            LOG.info("Player {} has mandatory cards: {}", player.getName(), mandatoryCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
            // Player with obligation cards is out of the game.
            setPlayerStatus(player, PlayerStatus.OUT_OF_GAME);
            // return properties to game
            getProperties(player).forEach(
                    x -> propertyOwnerRemove(x.getIndex())
            );
            // return chance cards to game if any
            getBackAllChanceCards(player);
            return;
        }
        // it seems that player in the game, so we need to clear his non-keepable cards
        getBackAllPlayerCards(player);
    }

    @Override
    public void doContract(Player player, int landId) throws TurnException, BankException {
        Property property = (Property) getLand(landId);
        if (getPropertyOwner(landId) != player) {
            throw new TurnException("Land is not owned by you");
        }
        LOG.info("Player {} is contracting property {} ({})", player.getName(), landId, property.getName());
        BigDecimal amount = property.getPrice();
        bank.deposit(player, amount);
        propertyOwnerRemove(landId);
    }

    @Override
    public List<IndexedEntry<Property>> getProperties(Player player) {
        List<Land> lands = board.getLands();
        List<IndexedEntry<Property>> result = new ArrayList<>();
        for (int i = 0; i < lands.size(); i++) {
            Land land = lands.get(i);
            if (land.getType() == Land.Type.PROPERTY && getPropertyOwner(i) == player) {
                Property property = (Property) land;
                result.add(new IndexedEntry<>(i, property));
            }
        }
        return result;
    }

    @Override
    public void buyProperty(Player player, int landId) throws TurnException, BankException {
        Property property = (Property) getLand(landId);
        BigDecimal price = property.getPrice();
        if (getPropertyOwner(landId) != null) {
            throw new TurnException("Land is already owned");
        }
        bank.withdraw(player, price);
        setPropertyOwner(landId, player);
    }

    @Override
    public BigDecimal getRent(int landId) {
        BigDecimal rent;
        Property property = (Property) getLand(landId);
        Player owner = getPropertyOwner(landId);
        if (owner == null) {
            rent = BigDecimal.valueOf(0);
            LOG.info("Land {} is not owned", landId);
        } else {
            List<Integer> sameColorLands = findLandsByColor(property.getColor());
            boolean sameColor = sameColorLands.stream()
                    .map(this::getPropertyOwner)
                    .allMatch(x -> x == owner);
            if (sameColor) {
                // double rent if the player owns all properties of the same color
                rent = property.getPrice().multiply(new BigDecimal(2));
                LOG.info("Player {} owns all properties of the same color {}, so the owner gets double rent: {}", owner.getName(), property.getColor(), rent);
            } else {
                rent = property.getPrice();
                LOG.info("Player {} owns property {}, so the owner gets rent: {}", owner.getName(), property.getName(), rent);
            }
        }
        return rent;
    }

    @Override
    public void pay(Player player, Player recipient, BigDecimal amount) throws BankException {
        LOG.info("{} is paying {} to {}", player.getName(), amount, recipient.getName());
        bank.withdraw(player, amount);
        bank.deposit(recipient, amount);
    }

    @Override
    public void payTax(Player player, BigDecimal amount) throws BankException {
        bank.withdraw(player, amount);

    }

    @Override
    public void leaveJail(Player player) throws TurnException {
        if (getStatus(player) != PlayerStatus.IN_JAIL) {
            throw new TurnException("Player is not in jail");
        }
        playerData.get(player).setStatus(PlayerStatus.IN_GAME);
    }

    @Override
    public void playerTurnStarted(Player player) {
        // there is no need to roll dice or move if the player did something in this turn
        playerData.get(player).getActionCards().removeIf(x -> x.getAction() == ActionCard.Action.NEW_TURN || x.getAction() == ActionCard.Action.ROLL_DICE);
    }

    @Override
    public BigDecimal getJailFine() {
        return board.getLands().stream()
                .filter(land -> land.getType() == Land.Type.JAIL)
                .map(x -> ((Jail) x).getFine())
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

}
