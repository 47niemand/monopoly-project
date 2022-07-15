package pp.muza.monopoly.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lombok.Data;
import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.pieces.actions.NewTurn;
import pp.muza.monopoly.model.pieces.actions.PayGift;
import pp.muza.monopoly.model.pieces.actions.PlayCard;
import pp.muza.monopoly.model.pieces.lands.Jail;
import pp.muza.monopoly.model.turn.TurnImpl;

/**
 * The game.
 * <p>
 * This class is responsible for managing the game.
 */

public class GameImpl implements Game {

    private static final Logger LOG = LoggerFactory.getLogger(GameImpl.class);
    private static final int DEFAULT_MAX_TURNS = 150;
    private final Bank bank;
    private final LinkedList<Fortune> fortuneCards;
    private final List<Player> players = new ArrayList<>();
    private final Map<Player, PlayerData> playerData = new HashMap<>();
    private final Map<Integer, Player> propertyOwners = new HashMap<>();
    private final Board board;
    int maxTurns = DEFAULT_MAX_TURNS;
    private int currentPlayerIndex;
    private int turnNumber;


    public GameImpl(Board board, List<Player> players, List<Fortune> fortunes, List<Strategy> strategies, Bank bank) {
        this.board = board;
        this.bank = bank;
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
        this.fortuneCards = new LinkedList<>(fortunes);
        Collections.shuffle(fortuneCards);
        currentPlayerIndex = 0;
    }

    public GameImpl(GameInfo gameInfo, List<Strategy> strategies, Bank bank) {
        this.board = gameInfo.getBoard();
        this.bank = bank;
        this.players.addAll(gameInfo.getPlayers());
        Iterator<Strategy> strategyIterator = strategies.iterator();
        Strategy strategy = null;

        Map<Player, PlayerData> playerData = new HashMap<>();
        for (PlayerInfo x : gameInfo.getPlayerInfo()) {
            if (strategyIterator.hasNext()) {
                strategy = strategyIterator.next();
            }
            PlayerData data = new PlayerData(x.getPlayer(), x.getStatus(), x.getPosition(), strategy, x.getActionCards());
            if (playerData.put(x.getPlayer(), data) != null) {
                throw new IllegalStateException("Duplicate key");
            }
            this.bank.set(x.getPlayer(), x.getMoney());

            for (IndexedEntry<Property> belonging : x.getBelongings()) {
                if (this.propertyOwners.put(belonging.getIndex(), x.getPlayer()) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }

        }
        this.playerData.putAll(playerData);
        this.fortuneCards = new LinkedList<>(gameInfo.getFortunes());
        this.currentPlayerIndex = gameInfo.getCurrentPlayerIndex();
        this.turnNumber = gameInfo.getTurnNumber();
        this.maxTurns = gameInfo.getMaxTurns();
    }

    List<Fortune> getFortuneCards() {
        return ImmutableList.copyOf(fortuneCards);
    }

    @Override
    public PlayerStatus getStatus(Player player) {
        return playerData.get(player).getStatus();
    }

    @Override
    public void birthdayParty(Player player) {
        players.stream()
                .filter(x -> x != player && !getStatus(x).isFinal())
                .forEach(x -> {
                    Turn subTurn = new TurnImpl(this, x);
                    sendCard(x, PayGift.of(player, BigDecimal.valueOf(1)));
                    playTurn(subTurn);
                });
    }

    @Override
    public void tradeProperty(Player player, Player salePlayer, int landId) throws BankException, TurnException {
        Property property = (Property) board.getLand(landId);
        if (getPropertyOwner(landId) != salePlayer) {
            throw new TurnException("Land is not owned by " + salePlayer.getName());
        }
        BigDecimal price = property.getPrice();
        withdraw(player, price);
        deposit(salePlayer, price);
        setPropertyOwner(landId, player);
    }

    public void setPlayerPosition(Player player, int position) {
        playerData.get(player).setPosition(position);
    }

    @Override
    public boolean playCard(Turn turn, ActionCard actionCard) throws TurnException {
        Player player = turn.getPlayer();
        boolean cardUsed;
        boolean newCardsSpawned;
        if (playerData.get(player).getActionCards().removeIf(x -> x.equals(actionCard))) {
            List<ActionCard> result = PlayCard.play(turn, actionCard);
            cardUsed = !result.contains(actionCard);
            // create a collection that does not contain cards that are already on player's hand
            List<ActionCard> newCards = result.stream().filter(x -> {
                boolean found = playerData.get(player).getActionCards().contains(x);
                if (found) {
                    LOG.debug("Card {} already on player's hand", x);
                }
                return !found;
            }).collect(Collectors.toList());
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
        } else {
            throw new TurnException("Action card not found");
        }
        return cardUsed || newCardsSpawned;
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
        List<Integer> path = getPathTo(getPosition(player), position);
        List<Land> lands = getLands(path);
        setPlayerPosition(player, position);
        return lands;
    }

    @Override
    public void addMoney(Player player, BigDecimal amount) throws BankException {
        bank.deposit(player, amount);
    }

    public List<ActionCard> getPlayerCards(Player player) {
        return ImmutableList.copyOf(playerData.get(player).getActionCards());
    }

    @Override
    public void sendCard(Player player, ActionCard actionCard) {
        playerData.get(player).getActionCards().add(actionCard);
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        List<Land> l = getLands();
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
    public BigDecimal getBalance(Player player) {
        return bank.getBalance(player);
    }

    @Override
    public Map<Integer, Player> getPropertyOwners() {
        return ImmutableMap.copyOf(propertyOwners);
    }


    public void playTurn(Turn turn) {
        Player player = turn.getPlayer();
        List<String> list = playerData.get(player).getActionCards().stream().map(ActionCard::getName)
                .collect(Collectors.toList());
        LOG.info("PlayerBehaviour's {} action cards: {}", player.getName(), list);
        Strategy strategy = playerData.get(player).getStrategy();
        strategy.playTurn(turn);
        if (!turn.isFinished()) {
            LOG.info("Player {} is not finished the turn", player);
            try {
                turn.endTurn();
            } catch (TurnException e) {
                LOG.error("Error in turn", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void gameLoop() {
        do {
            turnNumber++;
            Player player = getCurrentPlayer();
            LOG.info("PlayTurn {} - Player {}", turnNumber, player.getName());
            Turn turn = new TurnImpl(this, player);
            playerData.get(player).getActionCards().add(NewTurn.of());
            playTurn(turn);

            if (turnNumber >= maxTurns) {
                LOG.info("GameImpl loop ended after {} turns", turnNumber);
                break;
            }
        } while (nextPlayer());
        // get player with maximum money
        Player winner = players.stream()
                .filter(x -> !playerData.get(x).getStatus().isFinal())
                .max(Comparator.comparing(bank::getBalance))
                .orElseThrow(() -> new RuntimeException("No winner"));
        LOG.info("Winner: " + winner.getName());
        // print results
        players.forEach(x -> LOG.info("{} - {}", x.getName(), getPlayerInfo(x)));
    }

    @Override
    public GameInfo getGameInfo() {

        List<Player> players1 = ImmutableList.copyOf(getPlayers());
        ImmutableList.Builder<PlayerInfo> playerInfoBuilder = ImmutableList.builder();
        for (Player player : players1) {
            playerInfoBuilder.add(getPlayerInfo(player));
        }
        ImmutableList<PlayerInfo> playerInfo1 = playerInfoBuilder.build();
        Board board1 = getBoard();
        List<Fortune> fortuneCards1 = getFortuneCards();
        int currentPlayerIndex1 = getCurrentPlayerIndex();
        int turnNumber1 = getTurnNumber();
        int maxTurns1 = getMaxTurns();
        return new GameInfo(players1, playerInfo1, board1, fortuneCards1, currentPlayerIndex1, turnNumber1, maxTurns1);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    private int getNextPlayerId() {
        int temp = currentPlayerIndex;
        do {
            temp++;
            if (temp >= players.size()) {
                temp = 0;
            }
        } while (playerData.get(players.get(temp)).getStatus().isFinal() && temp != currentPlayerIndex);
        return temp;
    }

    boolean nextPlayer() {
        int temp = getNextPlayerId();
        boolean result = temp != currentPlayerIndex;
        currentPlayerIndex = temp;
        if (result) {
            LOG.info("Next player: {}", players.get(currentPlayerIndex).getName());
        } else {
            LOG.info("No next player");
        }
        return result;
    }

    @Override
    public List<ActionCard> getActiveActionCards(Player player) {
        List<ActionCard> result;
        List<ActionCard> actionCards = playerData.get(player).getActionCards();

        OptionalInt priority = playerData.get(player).getActionCards().stream()
                .filter(actionCard1 -> actionCard1.getType().isMandatory())
                .mapToInt(ActionCard::getPriority)
                .min();

        if (priority.isEmpty()) {
            priority = playerData.get(player).getActionCards().stream()
                    .mapToInt(ActionCard::getPriority)
                    .min();
        }

        int currentPriority = priority.orElse(ActionCard.LOW_PRIORITY);

        LOG.debug("PlayerBehaviour's {} current priority: {}", player.getName(), currentPriority);
        result = actionCards.stream()
                .filter(actionCard -> actionCard.getPriority() <= currentPriority)
                .sorted(Comparator.comparing(ActionCard::getPriority))
                .collect(Collectors.toList());

        return result;
    }

    PlayerInfo getPlayerInfo(Player player) {
        PlayerData playerData = this.playerData.get(player);
        List<Integer> playerProperties = propertyOwners.entrySet().stream()
                .filter(entry -> entry.getValue() == player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<IndexedEntry<Property>> belongings = playerProperties.stream().map(x -> new IndexedEntry<>(x, (Property) board.getLand(x)))
                .collect(Collectors.toList());
        return new PlayerInfo(player, playerData.getPosition(), playerData.getStatus(), bank.getBalance(player),
                ImmutableList.copyOf(playerData.getActionCards()), belongings);
    }

    List<Land> getLands() {
        return board.getLands();
    }

    List<Land> getLands(List<Integer> path) {
        return board.getLands(path);
    }

    public List<Integer> getPathTo(int startPos, int endPos) {
        return board.getPathTo(startPos, endPos);
    }

    public void deposit(Player player, BigDecimal amount) throws BankException {
        bank.deposit(player, amount);
    }

    @Override
    public Player getPropertyOwner(int landId) {
        return propertyOwners.get(landId);
    }

    public void withdraw(Player player, BigDecimal amount) throws BankException {
        bank.withdraw(player, amount);
    }

    public void setPropertyOwner(int landId, Player player) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now owned by {}", landId, property.getName(), player.getName());
        Player oldOwner = propertyOwners.put(landId, player);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    public void getBackChanceCard(ActionCard card) {
        if (card.getAction() != ActionCard.Action.CHANCE) {
            throw new IllegalArgumentException("Not a chance card");
        }
        assert card instanceof Fortune;
        Fortune fortune = (Fortune) card;
        LOG.info("Fortune card {} returned", card.getName());
        assert !fortuneCards.contains(fortune);
        fortuneCards.addLast(fortune);
    }

    public void propertyOwnerRemove(int landId) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now free", landId, property.getName());
        Player oldOwner = propertyOwners.remove(landId);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    @Override
    public int getStartPosition() {
        return board.getStartPosition();
    }

    @Override
    public int findProperty(Property.Asset asset) {
        for (int i = 0; i < getLands().size(); i++) {
            Land land = getLands().get(i);
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
        for (int i = 0; i < getLands().size(); i++) {
            Land land = getLands().get(i);
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

    void bringFortuneCardToTop(Fortune.Chance card) {
        Fortune fortune = removeFortuneCard(card);
        fortuneCards.addFirst(fortune);
    }

    Fortune removeFortuneCard(Fortune.Chance chance) {
        Fortune result;
        // find fortune by given chance
        OptionalInt index = IntStream.range(0, fortuneCards.size())
                .filter(i -> fortuneCards.get(i).getChance() == chance)
                .findFirst();
        if (index.isPresent()) {
            LOG.info("Fortune card {} removed from pile", chance.name());
            result = fortuneCards.remove(index.getAsInt());
        } else {
            LOG.error("Fortune card {} not found", chance.name());
            result = null;
        }
        return result;
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    public void getBackAllChanceCards(Player player) {
        playerData.get(player).actionCards.removeIf(x -> {
                    boolean found = false;
                    if (x.getAction() == ActionCard.Action.CHANCE) {
                        // return chance card to pile
                        getBackChanceCard(x);
                        found = true;
                    }
                    return found;
                }
        );
    }

    public void getBackAllPlayerCards(Player player) {
        playerData.get(player).actionCards.removeIf(x -> {
                    boolean found = x.getType() != ActionCard.Type.KEEPABLE;
                    if (found) {
                        LOG.info("{} lost action card {}", player.getName(), x.getName());
                        if (x.getAction() == ActionCard.Action.CHANCE) {
                            getBackChanceCard(x);
                        }
                    }
                    return found;
                }
        );
    }

    @Override
    public void endTurn(Player player) {
        List<ActionCard> playerCards = getPlayerCards(player);
        LOG.info("Not used cards: {}",
                playerCards.stream().map(ActionCard::getName).collect(Collectors.toList()));

        List<ActionCard> mandatoryCards = playerCards.stream()
                .filter(actionCard -> actionCard.getType().isMandatory())
                .collect(Collectors.toList());

        if (mandatoryCards.size() > 0) {
            LOG.info("Player {} has mandatory cards: {}", player, mandatoryCards);
            // player has mandatory cards, so he has lost the game
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
        deposit(player, amount);
        propertyOwnerRemove(landId);
    }

    void setPlayerStatus(Player player, PlayerStatus status) {
        playerData.get(player).setStatus(status);
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
    public void payRent(Player player, int landId) throws TurnException, BankException {
        Property property = (Property) getLand(landId);
        BigDecimal rent = property.getPrice();
        Player owner = getPropertyOwner(landId);
        if (owner == null) {
            throw new TurnException("Land is not owned");
        }
        LOG.info("{} pays {} to {} for {}", player.getName(), rent, owner.getName(), property.getName());
        bank.withdraw(player, rent);
        bank.deposit(owner, rent);
    }

    @Override
    public void pay(Player player, Player recipient, BigDecimal amount) throws BankException {
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
        playerData.get(player).actionCards.removeIf(x -> x.getAction() == ActionCard.Action.NEW_TURN || x.getAction() == ActionCard.Action.ROLL_DICE);
    }

    @Override
    public BigDecimal getJailFine() {
        return getLands().stream()
                .filter(land -> land.getType() == Land.Type.JAIL)
                .map(x -> ((Jail) x).getFine())
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    public Board getBoard() {
        return board;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void endGame() {
        LOG.info("GameImpl ended");
        for (Player player : players) {
            getBackAllChanceCards(player);
        }
    }

    @Data
    private static final class PlayerData {
        private final Player player;
        private final List<ActionCard> actionCards;
        private PlayerStatus status;
        private int position;
        private Strategy strategy;

        public PlayerData(Player player, PlayerStatus status, int position, Strategy strategy) {
            this.player = player;
            this.status = status;
            this.position = position;
            this.actionCards = new ArrayList<>();
            this.strategy = strategy;
        }

        public PlayerData(Player player, PlayerStatus status, int position, Strategy strategy, List<ActionCard> actionCards) {
            this(player, status, position, strategy);
            this.actionCards.addAll(actionCards);
        }

        public void setPosition(int position) {
            if (this.position != position) {
                LOG.info("{}: changing position from {} to {}", this.player.getName(), this.position, position);
                this.position = position;
            } else {
                LOG.info("{} at position {}", this.player.getName(), this.position);
            }
        }

        public void setStatus(PlayerStatus status) {
            assert status != null;
            if (this.status == null) {
                LOG.debug("{} set status to {}", player.getName(), status);
            } else {
                LOG.info("{}: changing status from {} to {}", this.player.getName(), this.status, status);
            }
            this.status = status;
        }
    }
}
