package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.consts.Meta;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.PlayGame;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.ActionType;
import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.NewTurn;
import pp.muza.monopoly.model.game.turn.TurnImpl;

public class BaseGame implements PlayGame {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGame.class);
    protected final LinkedList<Fortune> fortuneCards;
    protected final Map<Integer, Player> propertyOwners = new HashMap<>();
    private final Bank bank;
    private final List<Player> players;
    private final Map<Player, PlayerContext> playerState = new HashMap<>();
    private final Board board;

    private PlayTurn currentTurn;
    private int currentPlayerIndex = -1;
    private int turnNumber = 0;

    protected BaseGame(Bank bank, Board board, List<Fortune> fortuneCards, List<Player> players) {
        this.bank = bank;
        this.board = board;
        this.fortuneCards = new LinkedList<>(fortuneCards);
        this.players = new ArrayList<>(players);
        for (Player player : players) {
            PlayerContext playerContext = new PlayerContext(player);
            playerContext.setStatus(PlayerStatus.IN_GAME);
            playerContext.setPosition(board.getStartPosition());
            playerState.put(player, playerContext);
        }
        for (Player player : playerState.keySet()) {
            bank.set(player, Meta.STARTING_AMOUNT);
        }
    }

    public Bank getBank() {
        return bank;
    }

    int getNextPlayerIndex() {
        int temp = currentPlayerIndex;
        do {
            temp++;
            if (temp >= players.size()) {
                temp = 0;
            }
        } while (playerState.get(players.get(temp)).getStatus().isFinal() && temp != currentPlayerIndex);
        return temp;
    }

    void newTurn() {
        if (currentPlayerIndex < 0) {
            throw new IllegalStateException("Game is not started");
        }
        Player currentPlayer = players.get(currentPlayerIndex);
        currentTurn = (PlayTurn) TurnImpl.of((Game) this, currentPlayer, turnNumber);
        LOG.info("Player {} is starting turn {}", currentPlayer.getName(), turnNumber);
        LOG.debug("Info: {}", getPlayerInfo(currentPlayer));
    }


    protected List<IndexedEntry<Property>> belongings(Player player) {
        return propertyOwners.entrySet().stream()
                .filter(x -> x.getValue() == player)
                .map(x -> new IndexedEntry<>(x.getKey(), (Property) board.getLand(x.getKey())))
                .collect(Collectors.toUnmodifiableList());
    }

    public void getBackChanceCard(ActionCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (card.getAction() != Action.CHANCE) {
            throw new IllegalArgumentException("Not a chance card");
        }
        Fortune fortune = (Fortune) card;
        if (fortuneCards.contains(fortune)) {
            throw new IllegalArgumentException("Card already in deck");
        }
        LOG.info("Fortune card '{}; returned", card.getName());
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

    void getBackAllCards(Player player) {
        PlayerContext data = playerState.get(player);
        List<ActionCard> chanceCards = ImmutableList.copyOf(data.getCards());
        for (ActionCard card : chanceCards) {
            if (card.getAction() == Action.CHANCE) {
                getBackChanceCard(card);
            }
            data.removeCard(card);
        }
    }

    void getBackAllChanceCards(Player player) {
        PlayerContext data = playerState.get(player);
        List<ActionCard> chanceCards = data.getCards().stream().filter(x -> x.getAction() == Action.CHANCE && x.getType() != ActionType.KEEPABLE).collect(Collectors.toList());
        chanceCards.stream().map(data::removeCard).forEach(this::getBackChanceCard);
    }

    public void finishTurn(Player player) throws GameException {
        if (currentTurn == null || currentTurn.isFinished()) {
            throw new GameException("No turn in progress");
        }
        if (player != currentTurn.getPlayer()) {
            throw new GameException("Not current player");
        }
        LOG.debug("Finishing turn for {}", player.getName());

        PlayerContext data = playerState.get(player);
        data.releaseAll();
        getBackAllChanceCards(player);
        List<ActionCard> mandatoryCards = data.getCards().stream()
                .filter(actionCard -> actionCard.getType().isMandatory())
                .collect(Collectors.toList());
        if (mandatoryCards.size() > 0) {
            LOG.info("Player {} has mandatory cards: {}", player.getName(), mandatoryCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
            // Player with obligation cards is out of the game.
            data.setStatus(PlayerStatus.OUT_OF_GAME);

            // return properties to game
            for (IndexedEntry<Property> entry : belongings(player)) {
                propertyOwnerRemove(entry.getIndex());
            }

            // return chance cards to game if any
            getBackAllCards(player);
            return;
        }
        getBackAllChanceCards(player);
        currentTurn = null;
    }

    public PlayerContext playerContext(Player player) {
        return playerState.get(player);
    }


    //================================================================================================

    @Override
    public boolean isGameInProgress() {
        return (currentTurn != null && !currentTurn.isFinished()) || (getNextPlayerIndex() != currentPlayerIndex);
    }

    void nextPlayer() throws GameException {
        LOG.debug("Player {} is ending turn {}", players.get(currentPlayerIndex).getName(), turnNumber);
        int nextPlayerIndex = getNextPlayerIndex();
        if (nextPlayerIndex == currentPlayerIndex) {
            throw new GameException("No more players in game");
        }
        if (currentTurn != null && !currentTurn.isFinished()) {
            throw new IllegalStateException("Current turn is not finished");
        }
    }

    @Override
    public PlayTurn getTurn() throws GameException {
        if (currentTurn == null || currentTurn.isFinished()) {
            nextPlayer();
            newTurn();
            playerContext(currentTurn.getPlayer()).addCard(NewTurn.of());
        }
        return currentTurn;
    }

    @Override
    public List<ActionCard> getActiveCards(Player player) {
        PlayerContext data = playerState.get(player);
        List<ActionCard> result = data.getActiveCards();
        LOG.debug("Cards in play: {} for {}", result, player.getName());
        return result;
    }

    @Override
    public List<ActionCard> getCards(Player player) {
        return playerState.get(player).getCards();
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public PlayerInfo getPlayerInfo(Player player) {
        PlayerContext data = playerState.get(player);
        return PlayerInfo.builder()
                .player(player)
                .position(data.getPosition())
                .status(data.getStatus())
                .coins(bank.getBalance(player))
                .actionCards(data.getCards())
                .belongings(belongings(player))
                .build();
    }


    @Override
    public Map<Integer, Player> getPropertyOwners() {
        return Collections.unmodifiableMap(propertyOwners);
    }

    /**
     * for testing
     */
    void bringFortuneCardToTop(Chance card) {
        Fortune fortune = pickFortuneCard(card);
        fortuneCards.addFirst(fortune);
    }

    void setPropertyOwner(int landId, Player player) {
        Property property = (Property) getBoard().getLand(landId);
        LOG.info("Property {} ({}) is now owned by {}", landId, property.getName(), player.getName());
        Player oldOwner = propertyOwners.put(landId, player);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    //================================================================================================

    /**
     * for testing
     */
    Fortune pickFortuneCard(Chance chance) {
        Fortune result;
        // find fortune by given chance
        OptionalInt index = IntStream.range(0, fortuneCards.size())
                .filter(i -> fortuneCards.get(i).getChance() == chance)
                .findFirst();
        if (index.isPresent()) {
            LOG.info("Fortune card '{}' removed from pile", chance.name());
            result = fortuneCards.remove(index.getAsInt());
        } else {
            LOG.error("Fortune card '{}' not found", chance.name());
            result = null;
        }
        return result;
    }

    /**
     * for testing
     */
    void sendCard(Player to, ActionCard actionCard) {
        LOG.info("Sending card '{}' to {}", actionCard.getName(), to.getName());
        playerState.get(to).addCard(actionCard);
    }

    public void start() {
        LOG.info("Starting game");
        if (players.size() < 2) {
            throw new IllegalStateException("Not enough players");
        }
        if (players.size() > 4) {
            throw new IllegalStateException("Too many players");
        }
        if (currentPlayerIndex >= 0) {
            throw new IllegalStateException("Game already started");
        }
        currentPlayerIndex = 0;
    }

}
