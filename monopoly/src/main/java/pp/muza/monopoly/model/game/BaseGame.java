package pp.muza.monopoly.model.game;

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
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;
import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.NewTurn;

public abstract class BaseGame {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGame.class);

    private final LinkedList<Fortune> fortuneCards;
    private final Map<Integer, Player> propertyOwners = new HashMap<>();
    private final Bank bank;
    private final List<Player> players;
    private final Map<Player, PlayerData> playerData = new HashMap<>();
    private final Board board;
    private final BaseGame thisGame = this;

    private final Game game = new GameImpl() {
        @Override
        protected BaseGame baseGame() {
            return thisGame;
        }
    };
    BaseTurn currentTurn;
    private int currentPlayerIndex = -1;
    private int turnNumber = 0;
    private boolean started = false;

    public BaseGame(Bank bank, Board board, List<Fortune> fortuneCards, List<Player> players) {
        this.bank = bank;
        this.board = board;
        this.fortuneCards = new LinkedList<>(fortuneCards);
        this.players = ImmutableList.copyOf(players);
        for (Player player : players) {
            PlayerData info = new PlayerData(player);
            info.setStatus(PlayerStatus.IN_GAME);
            info.setPosition(board.getStartPosition());
            this.playerData.put(player, info);
        }
        for (Player player : playerData.keySet()) {
            bank.set(player, Meta.STARTING_AMOUNT);
        }
    }

    private int getNextPlayerIndex() {
        int temp = currentPlayerIndex;
        do {
            temp++;
            if (temp >= players.size()) {
                temp = 0;
            }
        } while (playerData.get(players.get(temp)).getStatus().isFinal() && temp != currentPlayerIndex);
        return temp;
    }

    void newTurn() {
        if (turnNumber > Meta.DEFAULT_MAX_TURNS) {
            LOG.error("Number of turns exceeded {}.", Meta.DEFAULT_MAX_TURNS);
            throw new RuntimeException("Too many turns.");
        }
        Player currentPlayer = players.get(currentPlayerIndex);
        turnNumber++;
        currentTurn = new BaseTurn(currentPlayer, turnNumber) {
            @Override
            protected BaseGame baseGame() {
                return thisGame;
            }
        };
        LOG.info("{} is starting turn {}", currentPlayer.getName(), turnNumber);
        LOG.info("Info: {}", getPlayerInfo(currentPlayer));
    }

    private void getBackAllCards(Player player) {
        PlayerData data = playerData.get(player);
        List<ActionCard> chanceCards = ImmutableList.copyOf(data.getCards());
        for (ActionCard card : chanceCards) {
            if (card.getAction() == Action.CHANCE) {
                getBackChanceCard(card);
            }
            data.removeCard(card);
        }
    }

    private void getBackAllChanceCards(Player player) {
        PlayerData data = playerData.get(player);
        List<ActionCard> chanceCards = data.getCards().stream().filter(x -> x.getAction() == Action.CHANCE && x.getType() != ActionType.KEEPABLE).collect(Collectors.toList());
        chanceCards.stream().map(data::removeCard).forEach(this::getBackChanceCard);
    }

    void nextPlayer() throws GameException {
        if (turnNumber > 0) {
            LOG.debug("{} is ending turn {}", players.get(currentPlayerIndex).getName(), turnNumber);
        }
        int nextPlayerIndex = getNextPlayerIndex();
        if (nextPlayerIndex == currentPlayerIndex) {
            throw new GameException("No more players in game");
        }
        if (currentTurn != null && !currentTurn.isFinished()) {
            throw new GameException("Current turn is not finished");
        }
        currentPlayerIndex = nextPlayerIndex;
    }

    private void checkStarted() throws GameException {
        if (!started) {
            throw new GameException("Game is not started");
        }
    }

    void getBackChanceCard(ActionCard card) {
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

    //================================================================================================

    public List<IndexedEntry<Property>> belongings(Player player) {
        return propertyOwners.entrySet().stream().filter(x -> x.getValue() == player).map(x -> new IndexedEntry<>(x.getKey(), (Property) board.getLand(x.getKey()))).collect(Collectors.toUnmodifiableList());
    }

    public void propertyOwnerRemove(int landId) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now free", landId, property.getName());
        Player oldOwner = propertyOwners.remove(landId);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    public PlayerData playerData(Player player) {
        return playerData.get(player);
    }

    public Bank getBank() {
        return bank;
    }

    public Fortune takeFortuneCard() {
        return fortuneCards.removeFirst();
    }

    public boolean isGameInProgress() {
        boolean inProgress = (currentTurn != null && !currentTurn.isFinished());
        inProgress |= (getNextPlayerIndex() != currentPlayerIndex);
        boolean lastPlayer = getPlayers().stream().filter(x -> !playerData.get(x).getStatus().isFinal()).count() < 2;
        return inProgress && started && !lastPlayer;
    }

    private void checkTurn(Turn turn) throws GameException {
        if (currentTurn == null || currentTurn.isFinished()) {
            throw new GameException("No turn in progress");
        }
        if (turn != currentTurn.getTurn()) {
            throw new GameException("Wrong turn");
        }
    }

    private void releaseTurn() {
        Player player = currentTurn.getPlayer();
        PlayerData data = playerData.get(player);
        data.releaseAll();
        getBackAllChanceCards(player);
    }

    public void holdTurn(Turn turn) throws GameException {
        checkTurn(turn);
        Player player = currentTurn.getPlayer();
        LOG.debug("{} is holding turn", turn.getPlayer().getName());
        releaseTurn();
        PlayerData data = playerData.get(player);
        List<ActionCard> mandatoryCards = data.getCards().stream().filter(actionCard -> actionCard.getType().isMandatory()).collect(Collectors.toList());
        if (mandatoryCards.size() > 0) {
            LOG.info("Player {} has mandatory cards: {}", player.getName(), mandatoryCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        }
        currentTurn.markFinished();
        currentTurn = null;
    }

    public void finishTurn(Turn turn) throws GameException {
        checkTurn(turn);
        Player player = currentTurn.getPlayer();
        LOG.debug("Finishing turn for {}", player.getName());
        releaseTurn();
        PlayerData data = playerData.get(player);
        List<ActionCard> mandatoryCards = data.getCards().stream().filter(actionCard -> actionCard.getType().isMandatory()).collect(Collectors.toList());
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
        currentTurn.markFinished();
        currentTurn = null;
    }

    public void start() throws GameException {
        if (players.size() < Meta.MIN_PLAYERS) {
            throw new GameException("Not enough players");
        }
        if (players.size() > Meta.MAX_PLAYERS) {
            throw new GameException("Too many players");
        }
        if (started) {
            throw new GameException("Game already started");
        }
        LOG.info("Starting game");
        started = true;
    }

    public PlayTurn getTurn() throws GameException {
        checkStarted();
        if (currentTurn == null || currentTurn.isFinished()) {
            nextPlayer();
            newTurn();
            Player currentPlayer = players.get(currentPlayerIndex);
            // check if the player has obligation cards with high priority
            boolean activeCards = playerData(currentPlayer).getActiveCards().stream()
                    .anyMatch(actionCard -> actionCard.getType().isMandatory() && actionCard.getPriority() <= BaseActionCard.NEW_TURN_PRIORITY);
            if (!activeCards) {
                // if not, then the player can start a new turn
                playerData(currentPlayer).addCard(NewTurn.of());
            }
        }
        return currentTurn;
    }

    public Game getGame() {
        return game;
    }

    public List<ActionCard> getActiveCards(Player player) {
        PlayerData data = playerData.get(player);
        List<ActionCard> result = data.getActiveCards();
        LOG.debug("Cards in play: {} for {}", result, player.getName());
        return result;
    }

    public List<ActionCard> getCards(Player player) {
        return playerData.get(player).getCards();
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public PlayerInfo getPlayerInfo(Player player) {
        PlayerData info = playerData.get(player);
        return PlayerInfo.builder().player(player).position(info.getPosition()).status(info.getStatus()).coins(bank.getBalance(player)).actionCards(info.getCards()).belongings(belongings(player)).build();
    }

    public Map<Integer, Player> getPropertyOwners() {
        return Collections.unmodifiableMap(propertyOwners);
    }

    public void setPropertyOwner(int landId, Player player) {
        Property property = (Property) getBoard().getLand(landId);
        LOG.info("Property {} ({}) is now owned by {}", landId, property.getName(), player.getName());
        Player oldOwner = propertyOwners.put(landId, player);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    public Player getPropertyOwner(int position) {
        return propertyOwners.get(position);
    }

    public int getTurnNumber() {
        return this.turnNumber;
    }

    //================================================================================================

    /**
     * for testing
     */
    void bringFortuneCardToTop(Chance card) {
        Fortune fortune = pickFortuneCard(card);
        fortuneCards.addFirst(fortune);
    }

    /**
     * for testing
     */
    Fortune pickFortuneCard(Chance chance) {
        Fortune result;
        // find fortune by given chance
        OptionalInt index = IntStream.range(0, fortuneCards.size()).filter(i -> fortuneCards.get(i).getChance() == chance).findFirst();
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
        playerData.get(to).addCard(actionCard);
    }

}
