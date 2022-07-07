package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardExecute;
import pp.muza.monopoly.model.actions.ChanceCard;
import pp.muza.monopoly.model.actions.cards.Chance;
import pp.muza.monopoly.model.actions.cards.NewTurn;
import pp.muza.monopoly.model.game.strategy.DefaultStrategy;
import pp.muza.monopoly.model.game.strategy.Strategy;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The game.
 * <p>
 * This class is responsible for managing the game.
 */
public class Game {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);
    private final Board<Land> board = BoardUtils.defaultBoard();
    private final Bank bank = new Bank();
    private final LinkedList<Chance> chanceCards = new LinkedList<>();
    private final List<Player> players = new ArrayList<>();
    private final Map<Player, PayerStatus> playerStatus = new HashMap<>();
    private final Map<Integer, Player> propertyOwner = new HashMap<>();
    int MAX_TURNS = 200;
    private int currentPlayerIndex;

    public Game(List<Player> players) {
        for (Player player : players) {
            playerStatus.put(player,
                    new PayerStatus(player, PlayerStatus.IN_GAME, getStartPosition(), DefaultStrategy.strategy));
            try {
                bank.addMoney(player, BigDecimal.valueOf(18));
            } catch (BankException e) {
                throw new RuntimeException(e);
            }
        }
        this.players.addAll(players);
        assert players.size() == playerStatus.keySet().size();
        chanceCards.addAll(Arrays
                .stream(ChanceCard.values())
                .map(Chance::of)
                .collect(Collectors.toList()));
        Collections.shuffle(chanceCards);
        currentPlayerIndex = 0;
    }

    public Game(List<Player> players, List<Strategy> strategies) {
        this(players);
        Iterator<Player> playerIterator = players.iterator();
        Iterator<Strategy> strategyIterator = strategies.iterator();
        Strategy strategy = null;
        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if (strategyIterator.hasNext()) {
                strategy = strategyIterator.next();
            }
            playerStatus.get(player).setStrategy(strategy);
        }
    }

    public void setPlayerStatus(Player player, PlayerStatus status) {
        playerStatus.get(player).setStatus(status);
    }

    public PlayerStatus getPlayerStatus(Player player) {
        return playerStatus.get(player).getStatus();
    }

    public void setPlayerPosition(Player player, int position) {
        playerStatus.get(player).setPosition(position);
    }

    public boolean executeActionCards(Turn turn, ActionCard actionCard) throws TurnException {
        Player player = turn.getPlayer();
        boolean cardUsed;
        boolean newCardsSpawned;
        if (playerStatus.get(player).getActionCards().removeIf(x -> x.equals(actionCard))) {
            List<ActionCard> result = ActionCardExecute.execute(turn, actionCard);
            cardUsed = !result.contains(actionCard);
            // create collection that do no contains card that are already on player's hand
            List<ActionCard> newCards = result.stream().filter(x -> {
                boolean found = playerStatus.get(player).getActionCards().contains(x);
                if (found) {
                    LOG.info("Card {} already on player's hand", x.getName());
                }
                return !found;
            }).collect(Collectors.toList());
            newCardsSpawned = (newCards.size() > 0 && cardUsed) || (newCards.size() > 1);
            playerStatus.get(player).getActionCards().addAll(newCards);

            // Chance cards (ActionCard.Type.CHANCE) can only be used once, thus we must take them out of the player's hand.
            if (actionCard.getType() == ActionCard.Type.CHANCE) {
                List<ActionCard> chances = playerStatus.get(player)
                        .getActionCards()
                        .stream()
                        .filter(x -> x.getType() == ActionCard.Type.CHANCE
                                && x.getPriority() <= actionCard.getPriority())
                        .collect(Collectors.toList());
                playerStatus.get(player).getActionCards().removeAll(chances);
                LOG.info("Removing chance cards from player's hand: {}", chances.stream().map(ActionCard::getName).collect(Collectors.toList()));
            }

            // return chance card (ActionCard.Action.CHANCE) to the game
            if (cardUsed && (actionCard.getAction() == ActionCard.Action.CHANCE)
                    && (actionCard.getType() != ActionCard.Type.KEEPABLE)) {
                returnChanceCard((Chance) actionCard);
            }
        } else {
            throw new TurnException("Action card not found");
        }
        return cardUsed || newCardsSpawned;
    }

    public int getNextPosition(Player player, int distance) {
        return board.getDestination(playerStatus.get(player).getPosition(), distance);
    }

    public int getPlayerPosition(Player player) {
        return playerStatus.get(player).getPosition();
    }

    public List<ActionCard> getPlayerCards(Player player) {
        return ImmutableList.copyOf(playerStatus.get(player).getActionCards());
    }

    public void sendCardToPlayer(Player player, ActionCard actionCard) {
        playerStatus.get(player).getActionCards().add(actionCard);
    }

    public void playTurn(Turn turn) {
        Player player = turn.getPlayer();
        List<String> list = playerStatus.get(player).getActionCards().stream().map(ActionCard::getName)
                .collect(Collectors.toList());
        LOG.info("Player's {} Action cards: {}", player.getName(), list);

        Strategy strategy = playerStatus.get(player).getStrategy();
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
        int turnNumber = 0;
        do {
            turnNumber++;
            Player player = getCurrentPlayer();
            LOG.info("Turn {} - Player {}", turnNumber, player.getName());
            Turn turn = new TurnImpl(this, player);
            playerStatus.get(player).getActionCards().add(NewTurn.of());
            playTurn(turn);

            if (turnNumber >= MAX_TURNS) {
                LOG.info("Game loop ended after {} turns", turnNumber);
                break;
            }
        } while (nextPlayer());
        // get player with maximum money
        Player winner = players.stream()
                .filter(x -> !playerStatus.get(x).getStatus().isFinal())
                .max(Comparator.comparing(bank::getBalance))
                .orElseThrow(() -> new RuntimeException("No winner"));
        LOG.info("Winner: " + winner.getName());
        // print results
        players.forEach(x -> LOG.info("{} - {}", x.getName(), getPlayerInfo(x)));
    }

    Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    boolean nextPlayer() {
        int temp = currentPlayerIndex;
        do {
            temp++;
            if (temp >= players.size()) {
                temp = 0;
            }
        } while (playerStatus.get(players.get(temp)).getStatus().isFinal() && temp != currentPlayerIndex);
        boolean result = temp != currentPlayerIndex;
        currentPlayerIndex = temp;
        if (result) {
            LOG.info("Next player: {}", players.get(currentPlayerIndex).getName());
        } else {
            LOG.info("No next player");
        }
        return result;
    }

    public List<ActionCard> getActiveActionCards(Player player) {
        List<ActionCard> result;
        List<ActionCard> actionCards = playerStatus.get(player).getActionCards();

        OptionalInt priority = playerStatus.get(player).getActionCards().stream()
                .filter(actionCard1 -> actionCard1.getType().isMandatory())
                .mapToInt(ActionCard::getPriority)
                .min();

        if (priority.isEmpty()) {
            priority = playerStatus.get(player).getActionCards().stream()
                    .mapToInt(ActionCard::getPriority)
                    .min();
        }

        int currentPriority = priority.orElse(ActionCard.LOW_PRIORITY);

        LOG.info("Current priority: {}", currentPriority);
        result = actionCards.stream()
                .filter(actionCard -> actionCard.getPriority() <= currentPriority)
                .sorted(Comparator.comparing(ActionCard::getPriority))
                .collect(Collectors.toList());

        return result;
    }

    PlayerInfo getPlayerInfo(Player player) {
        PayerStatus a = playerStatus.get(player);
        List<Integer> playersProperties = propertyOwner.entrySet().stream()
                .filter(entry -> entry.getValue() == player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<Property> belongings = playersProperties.stream().map(x -> (Property) board.getLand(x))
                .collect(Collectors.toList());
        return new PlayerInfo(player, a.getPosition(), a.getStatus(), bank.getBalance(player),
                ImmutableList.copyOf(a.getActionCards()), belongings);
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

    public void addMoney(Player player, BigDecimal amount) throws BankException {
        bank.addMoney(player, amount);
    }

    public Player getPropertyOwner(int landId) {
        return propertyOwner.get(landId);
    }

    public void withdraw(Player player, BigDecimal amount) throws BankException {
        bank.withdraw(player, amount);
    }

    public void setPropertyOwner(int landId, Player player) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now owned by {}", landId, property, player.getName());
        Player oldOwner = propertyOwner.put(landId, player);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    public void returnChanceCard(Chance card) {
        LOG.info("Chance card {} returned", card.getCard().name());
        assert !chanceCards.contains(card);
        chanceCards.addLast(card);
    }

    public void propertyOwnerRemove(int landId) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now free", landId, property);
        Player oldOwner = propertyOwner.remove(landId);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    public int getStartPosition() {
        return board.getStartPosition();
    }

    public Land getLand(int position) {
        return board.getLand(position);
    }

    public Chance popChanceCard() {
        return chanceCards.pop();
    }

    void bringChanceCardToTop(ChanceCard card) {
        Chance chance = removeChanceCard(card);
        chanceCards.addFirst(chance);
    }

    Chance removeChanceCard(ChanceCard card) {
        Chance result;
        // find chance by given card
        OptionalInt index = IntStream.range(0, chanceCards.size())
                .filter(i -> chanceCards.get(i).getCard() == card)
                .findFirst();
        if (index.isPresent()) {
            LOG.info("Chance card {} removed from pile", card.name());
            result = chanceCards.remove(index.getAsInt());
        } else {
            LOG.error("Chance card {} not found", card.name());
            result = null;
        }
        return result;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void resetPlayerCards(Player player) {
        playerStatus.get(player).actionCards.removeIf(x -> {
                    boolean found = x.getType() != ActionCard.Type.KEEPABLE;
                    if (found) {
                        LOG.info("{} lost action card {}", player.getName(), x.getName());
                    }
                    return found;
                }
        );
    }

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
                    x -> propertyOwnerRemove(x.getPosition())
            );
            // return chance cards to game if any
            playerCards.stream()
                    .filter(x -> x.getAction() == ActionCard.Action.CHANCE)
                    .forEach(x1 -> returnChanceCard((Chance) x1));
            return;
        }
        // it seems that player in game, so we need to clear his non-keepable cards
        resetPlayerCards(player);
    }

    public List<Land.Entry<Property>> getProperties(Player player) {
        List<Land> lands = board.getLands();
        List<Land.Entry<Property>> result = new ArrayList<>();
        for (int i = 0; i < lands.size(); i++) {
            Land land = lands.get(i);
            if (land.getType() == Land.Type.PROPERTY && getPropertyOwner(i) == player) {
                Property property = (Property) land;
                result.add(new Land.Entry<>(i, property));
            }
        }
        return result;
    }

    public void playerTurnStarted(Player player) {
        // there is no need to roll dice or move if player did something in this turn
        playerStatus.get(player).actionCards.removeIf(x -> x.getAction() == ActionCard.Action.NEW_TURN || x.getAction() == ActionCard.Action.ROLL_DICE);
    }

    @Data
    private static final class PayerStatus {
        private final Player player;
        private final List<ActionCard> actionCards;
        private PlayerStatus status;
        private int position;
        private Strategy strategy;

        public PayerStatus(Player player, PlayerStatus status, int position, Strategy strategy) {
            this.player = player;
            this.status = status;
            this.position = position;
            this.actionCards = new ArrayList<>();
            this.strategy = strategy;
        }

        public void setStrategy(Strategy strategy) {
            this.strategy = strategy;
        }

        public void setPosition(int position) {
            if (this.position != position) {
                LOG.info("{} changing position from {} to {}", this.player.getName(), this.position, position);
                this.position = position;
            } else {
                LOG.info("{} at position {}", this.player.getName(), this.position);
            }
        }

        public void setStatus(PlayerStatus status) {
            assert status != null;
            if (this.status == null) {
                LOG.info("set status to {}", status);
            } else {
                LOG.info("{} changing status from {} to {}", this.player.getName(), this.status, status);
            }
            this.status = status;
        }
    }
}
