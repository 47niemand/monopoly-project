package pp.muza.monopoly.model.game;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.lands.Property;

/**
 * The game.
 * <p>
 * The game is composed of a board, a list of players.
 * The board is composed of a list of lands.
 * There is a turn for each player. The turn is composed of a list of action
 * cards. (see {@link Turn})
 * Players can execute action cards. (see {@link ActionCard#execute(Turn) })
 * The player lost when he has mandatory action cards in his hand at the end of
 * the turn.
 * <p>
 */
public class Game {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private final Board<Land> board;
    private final Bank bank;
    private final List<Player> players;
    private final Map<Player, Integer> playerPos = new HashMap<>();
    private final Map<Integer, Player> landPlayerMap = new ConcurrentHashMap<>();
    private final Map<Player, PlayerStatus> playerStatus = new HashMap<>();
    private int currentPlayerId;

    private Turn currentTurn;

    public Game(Board<Land> board, Bank bank, List<Player> players) {
        this.board = board;
        this.bank = bank;
        this.players = ImmutableList.copyOf(players);
        try {
            initialize();
        } catch (BankException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get the winner(s) of the game.
     * it is players who have the highest amount of money, and they are in the game
     */
    public List<Player> getWinners() {
        // get the maximum amount of money of the players
        BigDecimal maxAmount = players.stream().map(bank::getBalance).max(BigDecimal::compareTo).orElseThrow();
        // get the players with the maximum amount of money
        return players.stream().filter(p -> bank.getBalance(p).compareTo(maxAmount) == 0).collect(Collectors.toList());
    }

    /**
     * Game loop.
     * <p>
     * The game loop is composed of a turn for each player.
     * Player's implementation of the turn is delegated to the player itself (see
     * {@link Player#playTurn(Turn)})
     */
    public void gameLoop() {
        int maxTurns = 100;
        int turn = 0;
        LOG.info("Game loop started");
        do {
            turn++;
            LOG.info("New turn {}", turn);
            currentTurn = new Turn(this, getCurrentPlayer());
            currentTurn.getPlayer().playTurn(currentTurn);
            if (!currentTurn.isFinished()) {
                // finish the turn
                endTurn(currentTurn);
            }

            if (turn >= maxTurns) {
                LOG.info("Game loop ended");
                break;
            }
        } while (nextPlayer());
        LOG.info("Game ended at turn {}", turn);
        getWinners().forEach(player -> {
            setPlayerStatus(player, PlayerStatus.WINNER);
            LOG.info("Player {} won", player.getName());
        });
        printStatistics();
    }

    private void printStatistics() {
        System.out.println("Statistics:");
        players.forEach(player -> {
            System.out.printf("Player %s%s balance: %s%n", player.getName(),
                    getPlayerStatus(player) == PlayerStatus.WINNER ? " (winner)" : "",
                    bank.getBalance(player));
            List<Integer> i = getPlayerProperties(player);
            List<Land> l = board.getLands(i);
            System.out.printf("Player %s has [%s]%n",
                    player.getName(),
                    l.stream().map(x -> String.format("%s (Price=%s)", x.getName(), ((Property) x).getPrice()))
                            .collect(Collectors.joining(", ")));

        });
    }

    public List<Integer> getPlayerProperties(Player player) {
        return this.landPlayerMap.keySet().stream().filter(landId -> player.equals(landPlayerMap.get(landId)))
                .collect(Collectors.toList());
    }

    private boolean nextPlayer() {
        int temp = currentPlayerId;
        do {
            temp++;
            if (temp >= players.size()) {
                temp = 0;
            }
        } while (playerStatus.get(players.get(temp)).isFinal && temp != currentPlayerId);
        boolean result = temp != currentPlayerId;
        currentPlayerId = temp;
        return result;
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerId);
    }

    /**
     * Initialize the game.
     * <p>
     * Initialize the player position.
     * Initialize the belongings of the players.
     * Initialize the player status.
     * Initialize the bank, and give each player the initial amount of money.
     * Initialize the position of the players on the board.
     * Initialize the current player id.
     */
    public void initialize() throws BankException {
        playerPos.clear();
        landPlayerMap.clear();
        playerStatus.clear();
        bank.reset();
        // bank add money to players
        for (Player p : players) {
            bank.addMoney(p, BigDecimal.valueOf(18));
        }
        currentPlayerId = 0;
        for (Player player : this.players) {
            playerPos.put(player, 0);
            playerStatus.put(player, PlayerStatus.IN_GAME);
        }
    }

    public int getJailPos() {
        List<Land> lands = board.getLands();
        return IntStream.range(0, lands.size()).filter(i -> lands.get(i).getType() == Land.Type.JAIL).findFirst()
                .orElseThrow();
    }

    public Board<Land> getBoard() {
        return board;
    }

    public PlayerStatus getPlayerStatus(Player player) {
        return playerStatus.get(player);
    }

    public void setPlayerStatus(Player player, PlayerStatus playerStatus) {
        LOG.info("Player {} status changed to {}", player.getName(), playerStatus);
        this.playerStatus.put(player, playerStatus);
    }

    public int getPlayerPos(Player player) {
        return playerPos.get(player);
    }

    public boolean isOwned(int pos) {
        return landPlayerMap.containsKey(pos);
    }

    public void setPlayerPos(Player player, int pos) {
        LOG.info("Player {} moved to {} ({})", player, pos, board.getLands().get(pos).getName());
        if (board.getLands().get(pos).getType() == Land.Type.PROPERTY) {
            LOG.info("The {} is owned by {}", board.getLands().get(pos).getName(), landPlayerMap.get(pos));
        }
        playerPos.put(player, pos);
    }

    public Player getLandPlayer(int landId) {
        return landPlayerMap.get(landId);
    }

    public int rollDice() {
        return (int) (Math.random() * 6) + 1;
    }

    /**
     * Pay the tax to the bank.
     *
     * @param player the player who pay the tax
     * @param amount the amount of money to pay
     * @throws BankException if the player doesn't have enough money
     */
    public void payTax(Player player, BigDecimal amount) throws BankException {
        bank.withdraw(player, amount);
    }

    /**
     * Pay the rent to the player.
     *
     * @param player the player who pay the rent
     * @param owner  the owner of the property
     * @param amount the amount of money to pay
     * @throws BankException if the player doesn't have enough money
     */
    public void payRent(Player player, Player owner, BigDecimal amount) throws BankException {
        bank.withdraw(player, amount);
        bank.addMoney(owner, amount);
    }

    /**
     * Buy the property.
     *
     * @param player   the player who buy the property
     * @param landId   the id of the property
     * @param property the property to buy
     * @throws BankException if the player doesn't have enough money
     */
    public void buyProperty(Player player, int landId, Property property) throws BankException {
        assert getBoard().getLand(landId) == property;
        LOG.info("Player {} is buying property {}...", player, property);
        if (isOwned(landId)) {
            throw new IllegalStateException("Property is already owned");
        }
        bank.withdraw(player, property.getPrice());
        landPlayerMap.put(landId, player);
        LOG.info("Player {} bought property {}", player, property);
    }

    /**
     * Add money to the player.
     *
     * @param player the player who receives the money
     * @param amount the amount of money to add
     * @throws BankException if the player doesn't allow receiving money
     */
    public void addMoney(Player player, BigDecimal amount) throws BankException {
        bank.addMoney(player, amount);
    }

    /**
     * End the turn.
     * <p>
     * The turn is finished when the player has completed his mandatory action
     * cards.
     * If there are mandatory action cards, the player lost the game and the all
     * belonging to him are returned to the bank.
     * </p>
     *
     * @param turn the current turn
     */
    public void endTurn(Turn turn) {
        assert currentTurn == turn;
        List<ActionCard> mandatoryActionCards = turn.getActiveActionCards().stream()
                .filter(actionCard -> actionCard.isMandatory()
                        && actionCard.getType() != ActionCard.ActionType.END_TURN)
                .collect(Collectors.toList());

        if (mandatoryActionCards.size() > 0) {
            // ask player to choose action card
            LOG.info("Player {} has mandatory action cards: {}", turn.getPlayer(), mandatoryActionCards);
            LOG.info("Player {} has lost the game", turn.getPlayer());
            setPlayerStatus(turn.getPlayer(), PlayerStatus.OUT_OF_GAME);
            // remove belongs from player
            landPlayerMap.keySet()
                    .stream()
                    .filter(landId -> landPlayerMap.get(landId).equals(turn.getPlayer()))
                    .forEach(landId -> removePlayerProperty(turn.getPlayer(), landId));
        } else {
            LOG.info("Ending turn for player {}", turn.getPlayer());
            turn.endTurn();
        }
    }

    /**
     * Remove the property from the player.
     *
     * @param player the player who owns the property
     * @param landId the id of the property
     */
    private void removePlayerProperty(Player player, Integer landId) {
        assert landPlayerMap.get(landId).equals(player);
        landPlayerMap.remove(landId);
        LOG.info("Player {} lost property {}", player, board.getLand(landId));
    }

    /**
     * Completes the contract for the player.
     *
     * @param player   the player
     * @param landId   the land id
     * @param property the property on the land
     * @param amount   the amount to pay
     * @throws BankException if the player does not allow to receive the money
     */
    public void contract(Player player, int landId, Property property, BigDecimal amount) throws BankException {
        LOG.info("Player {} is contracting {} for {}", player, property, amount);
        assert board.getLand(landId) == property;
        bank.addMoney(player, amount);
        Player p = landPlayerMap.remove(landId);
        assert p == player;
        LOG.info("Player {} sold property {}", player, property);
    }

    /**
     * get the land on the board at the given position
     *
     * @param pos the position
     * @return the land
     */
    public Land getLand(int pos) {
        return board.getLand(pos);
    }

    public enum PlayerStatus {
        IN_GAME(false),
        IN_JAIL(false),
        OUT_OF_GAME(true),
        WINNER(true);

        private final boolean isFinal;

        PlayerStatus(boolean isFinal) {
            this.isFinal = isFinal;
        }
    }
}
