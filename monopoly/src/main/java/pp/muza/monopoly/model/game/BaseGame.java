package pp.muza.monopoly.model.game;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.Data;
import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Strategy;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.actions.NewTurn;

abstract class BaseGame {

    static final Logger LOG = LoggerFactory.getLogger(BaseGame.class);

    static final int DEFAULT_MAX_TURNS = 150;
    final Bank bank;
    final LinkedList<Fortune> fortuneCards;
    final List<Player> players = new ArrayList<>();
    final Map<Player, PlayerData> playerData = new HashMap<>();
    final Map<Integer, Player> propertyOwners = new HashMap<>();
    final Board board;
    int currentPlayerIndex;
    int turnNumber;
    int maxTurns = DEFAULT_MAX_TURNS;

    protected BaseGame(Bank bank, List<Fortune> fortuneCards, Board board) {
        this.bank = bank;
        this.fortuneCards = new LinkedList<>(fortuneCards);
        this.board = board;
    }

    protected BaseGame(GameInfo gameInfo, List<Strategy> strategies, Bank bank) {
        this(bank, gameInfo.getFortunes(), gameInfo.getBoard());
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
        this.currentPlayerIndex = gameInfo.getCurrentPlayerIndex();
        this.turnNumber = gameInfo.getTurnNumber();
        this.maxTurns = gameInfo.getMaxTurns();
    }

    List<Fortune> getFortuneCards() {
        return ImmutableList.copyOf(fortuneCards);
    }

    List<ActionCard> getPlayerCards(Player player) {
        return ImmutableList.copyOf(playerData.get(player).getActionCards());
    }

    void playTurn(Turn turn) {
        Player player = turn.getPlayer();
        List<String> list = playerData.get(player).getActionCards().stream().map(ActionCard::getName)
                .collect(Collectors.toList());
        LOG.info("Player's {} action cards: {}", player.getName(), list);
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

    boolean nextPlayer() {
        int temp = currentPlayerIndex;
        do {
            temp++;
            if (temp >= players.size()) {
                temp = 0;
            }
        } while (playerData.get(players.get(temp)).getStatus().isFinished() && temp != currentPlayerIndex);
        int nextPlayerId = temp;
        boolean result = nextPlayerId != currentPlayerIndex;
        currentPlayerIndex = nextPlayerId;
        if (result) {
            LOG.info("Next player: {}", players.get(currentPlayerIndex).getName());
        } else {
            LOG.info("No next player");
        }
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

    void setPropertyOwner(int landId, Player player) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now owned by {}", landId, property.getName(), player.getName());
        Player oldOwner = propertyOwners.put(landId, player);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    void getBackChanceCard(ActionCard card) {
        if (card.getAction() != ActionCard.Action.CHANCE) {
            throw new IllegalArgumentException("Not a chance card");
        }
        assert card instanceof Fortune;
        Fortune fortune = (Fortune) card;
        LOG.info("Fortune card {} returned", card.getName());
        assert !fortuneCards.contains(fortune);
        fortuneCards.addLast(fortune);
    }

    void propertyOwnerRemove(int landId) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now free", landId, property.getName());
        Player oldOwner = propertyOwners.remove(landId);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
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

    void getBackAllChanceCards(Player player) {
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

    void getBackAllPlayerCards(Player player) {
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

    void setPlayerStatus(Player player, PlayerStatus status) {
        playerData.get(player).setStatus(status);
    }

    void endGame() {
        LOG.info("Game ended");
        for (Player player : players) {
            getBackAllChanceCards(player);
        }
    }

    public void gameLoop() {
        do {
            turnNumber++;
            Player player = players.get(currentPlayerIndex);
            GameImpl.LOG.info("PlayTurn {} - Player {}", turnNumber, player.getName());
            Turn turn = turn(player);
            playerData.get(player).getActionCards().add(NewTurn.of());
            playTurn(turn);

            if (turnNumber >= maxTurns) {
                GameImpl.LOG.info("Game loop ended after {} turns", turnNumber);
                break;
            }
        } while (nextPlayer());
        // get player with maximum money
        Player winner = players.stream()
                .filter(x -> !playerData.get(x).getStatus().isFinished())
                .max(Comparator.comparing(bank::getBalance))
                .orElseThrow(() -> new RuntimeException("No winner"));
        GameImpl.LOG.info("Winner: " + winner.getName());
        // print results
        players.forEach(x -> GameImpl.LOG.info("{} - {}", x.getName(), getPlayerInfo(x)));
    }


    abstract Turn turn(Player player);

    @Data
    protected static final class PlayerData {
        private final Player player;
        private final List<ActionCard> actionCards;
        private PlayerStatus status;
        private int position;
        private Strategy strategy;

        PlayerData(Player player, PlayerStatus status, int position, Strategy strategy) {
            this.player = player;
            this.status = status;
            this.position = position;
            this.actionCards = new ArrayList<>();
            this.strategy = strategy;
        }

        PlayerData(Player player, PlayerStatus status, int position, Strategy strategy, List<ActionCard> actionCards) {
            this(player, status, position, strategy);
            this.actionCards.addAll(actionCards);
        }

        void setPosition(int position) {
            if (this.position != position) {
                LOG.info("{}: changing position from {} to {}", this.player.getName(), this.position, position);
                this.position = position;
            } else {
                LOG.info("{} at position {}", this.player.getName(), this.position);
            }
        }

        void setStatus(PlayerStatus status) {
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
