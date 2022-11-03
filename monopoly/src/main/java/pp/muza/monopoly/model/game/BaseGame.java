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

import pp.muza.monopoly.consts.Constants;
import pp.muza.monopoly.consts.RuleOption;
import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.errors.GameError;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.bank.BankImpl;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.NewTurn;
import pp.muza.monopoly.model.pieces.lands.LandType;

/**
 * The base implementation of the game.
 * The class is not intended to be used directly.
 *
 * @author dmytromuza
 */
public abstract class BaseGame {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGame.class);

    private final Map<RuleOption, String> ruleOptions = new HashMap<>();
    private final LinkedList<Fortune> fortuneCards;
    private final Map<Integer, Player> propertyOwners = new HashMap<>();
    private final Bank bank;
    private final ImmutableList<Player> players;
    private final Map<Player, PlayerData> playerData = new HashMap<>();
    private final Board board;
    private final Game game = new GameImpl(this);
    private final BaseGame thisGame = this;

    BaseTurn currentTurn;
    int currentPlayerIndex = -1;
    int turnNumber = 0;
    int maxTurns = Constants.DEFAULT_MAX_TURNS;
    private boolean started = false;

    BaseGame(GameInfo gameInfo) {
        this.bank = new BankImpl();
        this.board = gameInfo.getBoard();
        this.fortuneCards = new LinkedList<>(gameInfo.getFortunes());
        this.players = ImmutableList.copyOf(gameInfo.getPlayers());
        this.maxTurns = gameInfo.getMaxTurns();
        this.currentPlayerIndex = gameInfo.getCurrentPlayerIndex();
        this.turnNumber = gameInfo.getTurnNumber();
        for (Player player : this.players) {
            PlayerData data = new PlayerData(player);
            PlayerInfo playerInfo = gameInfo.getPlayerInfo().stream()
                    .filter(x -> x.getPlayer().equals(player))
                    .findFirst()
                    .orElseThrow();
            bank.set(player, playerInfo.getCoins());
            for (Integer position : playerInfo.getBelongings()) {
                Land land = board.getLand(position);
                if (land.getType() != LandType.PROPERTY) {
                    throw new IllegalStateException("Land is not a property");
                }
                assert land instanceof Property;
                Player oldOwner = propertyOwners.put(position, player);
                if (oldOwner != null) {
                    throw new IllegalStateException("Property " + position + " is already owned by " + oldOwner);
                }
            }
            for (ActionCard actionCard : playerInfo.getActionCards()) {
                data.addCard(actionCard);
            }
            data.setStatus(playerInfo.getStatus());
            data.setPosition(playerInfo.getPosition());
            playerData.put(player, data);
        }
        started = currentPlayerIndex >= 0;
        if (started) {
            turnNumber--;
            newTurn();
        }
    }

    BaseGame(Bank bank, Board board, List<Fortune> fortuneCards, List<Player> players) {
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
        if (playerData.size() != players.size()) {
            throw new IllegalArgumentException("Duplicate players");
        }
        for (Player player : playerData.keySet()) {
            bank.set(player, Constants.STARTING_AMOUNT);
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

    private void newTurn() {
        if (turnNumber > maxTurns) {
            LOG.error("Number of turns exceeded {}.", maxTurns);
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
        LOG.info("{} is starting turn {}", currentPlayer, turnNumber);
        LOG.debug("Player's cards: {} ", playerData.get(currentPlayer).getCards());
    }

    private void getBackChanceCards(Player player, boolean includeKeepable) {
        PlayerData data = playerData.get(player);
        List<ActionCard> chanceCards = data.getCards().stream()
                .filter(x ->
                        x.getAction() == Action.CHANCE && (includeKeepable || x.getType() != ActionType.KEEPABLE))
                .collect(Collectors.toList());
        chanceCards.stream().map(data::removeCard).forEach(this::getBackChanceCard);
    }

    private void nextPlayer() throws GameException {
        int nextPlayerIndex = getNextPlayerIndex();
        if (nextPlayerIndex == currentPlayerIndex) {
            LOG.error("No more players in the game.");
            throw new GameException(GameError.NO_MORE_PLAYERS_IN_GAME);
        }
        if (currentTurn != null && !currentTurn.isFinished()) {
            LOG.error("Current turn is not finished.");
            throw new GameException(GameError.TURN_NOT_FINISHED);
        }
        currentPlayerIndex = nextPlayerIndex;
    }

    private void checkStarted() throws GameException {
        if (!started) {
            throw new GameException(GameError.GAME_NOT_STARTED);
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
        LOG.info("Fortune card '{}; returned", card);
        fortuneCards.addLast(fortune);
    }

    //================================================================================================

    List<Integer> belongings(Player player) {
        return propertyOwners.entrySet().stream()
                .filter(x -> x.getValue() == player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableList());
    }

    void propertyOwnerRemove(int position) {
        Property property = (Property) board.getLand(position);
        Player oldOwner = propertyOwners.remove(position);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner, position, property);
            LOG.info("Property {} ({}) is now free", position, property);
        } else {
            LOG.warn("Property {} ({}) is already free", position, property);
        }
    }

    PlayerData playerData(Player player) {
        return playerData.get(player);
    }

    Bank getBank() {
        return bank;
    }

    Fortune takeFortuneCard() {
        return fortuneCards.removeFirst();
    }

    boolean isGameInProgress() {
        boolean inProgress = (currentTurn != null && !currentTurn.isFinished());
        inProgress |= (getNextPlayerIndex() != currentPlayerIndex);
        boolean lastPlayer = getPlayers().stream().filter(x -> !playerData.get(x).getStatus().isFinal()).count() < 2;
        return inProgress && started && !lastPlayer;
    }

    private void checkTurn(Turn turn) throws GameException {
        if (currentTurn == null || currentTurn.isFinished()) {
            throw new GameException(GameError.NO_TURN_IN_PROGRESS);
        }
        if (turn != currentTurn.getTurn()) {
            throw new GameException(GameError.WRONG_TURN);
        }
    }

    private void releaseTurn() {
        Player player = currentTurn.getPlayer();
        PlayerData data = playerData.get(player);
        data.releaseAll();
    }

    void holdTurn(Turn turn) throws GameException {
        checkTurn(turn);
        Player player = currentTurn.getPlayer();
        LOG.debug("{} is holding turn", turn.getPlayer());
        releaseTurn();
        PlayerData data = playerData.get(player);
        List<ActionCard> mandatoryCards = data.getCards().stream().filter(actionCard -> actionCard.getType().isMandatory()).collect(Collectors.toList());
        if (mandatoryCards.size() > 0) {
            LOG.info("Player {} has mandatory cards: {}", player, mandatoryCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        }
        currentTurn.markFinished();
        currentTurn = null;
    }

    void finishTurn(Turn turn) throws GameException {
        checkTurn(turn);
        Player player = currentTurn.getPlayer();
        LOG.debug("Finishing turn for {}", player);
        releaseTurn();
        PlayerData data = playerData.get(player);
        List<ActionCard> mandatoryCards = data.getCards().stream().filter(actionCard -> actionCard.getType().isMandatory()).collect(Collectors.toList());
        ActionCard endTurn = mandatoryCards.stream().filter(actionCard -> actionCard.getAction() == Action.END_TURN).findFirst().orElse(null);
        if (endTurn != null) {
            data.removeCard(endTurn);
            mandatoryCards.remove(endTurn);
        }
        currentTurn.markFinished();
        if (mandatoryCards.size() > 0) {
            LOG.info("Player {} has mandatory cards: {}", player, mandatoryCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
            // Player with obligation cards is out of the game.
            data.setStatus(PlayerStatus.OUT_OF_GAME);

            // return properties to game
            for (Integer entry : belongings(player)) {
                propertyOwnerRemove(entry);
            }

            // return chance cards to game if any
            getBackChanceCards(player, true);
            return;
        }
        getBackChanceCards(player, false);
        currentTurn = null;
    }

    void start() throws GameException {
        if (players.size() < Constants.MIN_PLAYERS) {
            throw new GameException(GameError.NOT_ENOUGH_PLAYERS);
        }
        if (players.size() > Constants.MAX_PLAYERS) {
            throw new GameException(GameError.TOO_MANY_PLAYERS);
        }
        if (started) {
            throw new GameException(GameError.GAME_ALREADY_STARTED);
        }
        LOG.info("Starting game");
        started = true;
    }

    PlayTurn getTurn() throws GameException {
        checkStarted();
        if (currentTurn == null || currentTurn.isFinished()) {
            nextPlayer();
            newTurn();
            Player currentPlayer = players.get(currentPlayerIndex);
            // check if the player has end turn card
            boolean endTurn = playerData(currentPlayer).getCards().stream()
                    .anyMatch(actionCard -> actionCard.getAction() == Action.END_TURN);
            if (!endTurn) {
                // if not, then the player can start a new turn
                playerData(currentPlayer).addCard(NewTurn.create());
            }
        }
        return currentTurn.playTurn();
    }

    Game getGame() {
        return game;
    }

    List<ActionCard> getActiveCards(Player player) {
        PlayerData data = playerData.get(player);

        /*
          TODO: 11.10.2022
           There is a scenario in which keepable cards with high priority are available for execution.
           Actually, they can be executed depending on the player's state. For example,
           if the player is not in jail, he can't use {@link Chance#GET_OUT_OF_JAIL_FREE}
           to get out of jail. Currently, the player can try to use such cards only once a turn.
           if cards cannot be used, they are moved to hold, until the next turn
           - The solution is to check the player's state and filter out cards that can't be executed.
         */
        return data.getActiveCards();
    }

    List<ActionCard> getCards(Player player) {
        return playerData.get(player).getCards();
    }

    Board getBoard() {
        return board;
    }

    List<Player> getPlayers() {
        return players;
    }

    PlayerInfo getPlayerInfo(Player player) {
        PlayerData data = playerData.get(player);
        return PlayerInfo.builder()
                .player(player)
                .position(data.getPosition())
                .status(data.getStatus())
                .coins(bank.getBalance(player))
                .actionCards(data.getCards())
                .belongings(belongings(player))
                .build();
    }

    Map<Integer, Player> getPropertyOwners() {
        return Collections.unmodifiableMap(propertyOwners);
    }

    void setPropertyOwner(int position, Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        Property property = (Property) getBoard().getLand(position);
        Player oldOwner = propertyOwners.put(position, player);
        if (oldOwner == player) {
            LOG.warn("Player {} already owns property {} ({})", player, position, property);
        } else if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner, position, property);
        } else {
            LOG.info("Property {} ({}) is now owned by {}", position, property, player);
        }
    }

    Player getPropertyOwner(int position) {
        return propertyOwners.get(position);
    }

    int getTurnNumber() {
        return this.turnNumber;
    }

    GameInfo getGameInfo() {
        return GameInfo.builder()
                .players(getPlayers())
                .playerInfo(players.stream().map(this::getPlayerInfo).collect(Collectors.toList()))
                .board(board)
                .fortunes(ImmutableList.copyOf(fortuneCards))
                .currentPlayerIndex(currentPlayerIndex)
                .turnNumber(turnNumber)
                .maxTurns(maxTurns)
                .build();
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
    void sendCardTest(Player to, ActionCard actionCard) {
        LOG.info("Sending card '{}' to {}", actionCard, to);
        playerData.get(to).addCard(actionCard);
    }

    public String getRuleOptions(RuleOption option) {
        return ruleOptions.get(option);
    }

    public void setRule(RuleOption option, String value) {
        ruleOptions.put(option, value);
    }


}
