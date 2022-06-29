package pp.muza.monopoly.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.cards.chance.Chance;
import pp.muza.monopoly.model.actions.cards.chance.ChanceCard;
import pp.muza.monopoly.model.game.strategy.DefaultStrategy;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.turn.Turn;
import pp.muza.monopoly.model.turn.TurnImpl;

/**
 * The game.
 * <p>
 * The game is composed of a board, a list of players.
 * The board is composed of a list of lands.
 * There is a turn for each player. The turn is composed of a list of action
 * cards. (see {@link TurnImpl})
 * Players can execute action cards. (see {@link ActionCard#execute(Turn) })
 * The player lost when he has mandatory action cards in his hand at the end of
 * the turn.
 * <p>
 */
public class Game {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private final Board<Land> board;
    private final Bank bank;
    private final Pile<Chance> chanceCards;
    private final List<Player> players;
    private final Map<Player, DefaultStrategy> strategyMap = new HashMap<>();
    private final Map<Player, Integer> playerPos = new HashMap<>();
    private final Map<Integer, Player> landOwnerMap = new HashMap<>();
    private final Map<Player, PlayerStatus> playerStatus = new HashMap<>();
    private final Map<Player, Pile<ActionCard>> playerChanceCards = new HashMap<>();

    private int currentPlayerId;
    private Turn currentTurn;

    public Game(Board<Land> board, Bank bank, List<Player> players) {
        this.board = board;
        this.bank = bank;
        this.players = ImmutableList.copyOf(players);

        playerPos.clear();
        landOwnerMap.clear();
        playerStatus.clear();
        this.bank.reset();
        // bank add money to players
        for (Player p : this.players) {
            try {
                this.bank.addMoney(p, BigDecimal.valueOf(18));
            } catch (BankException e) {
                throw new RuntimeException(e);
            }
            strategyMap.put(p, new DefaultStrategy());
        }
        currentPlayerId = 0;
        for (Player player : this.players) {
            playerPos.put(player, 0);
            playerStatus.put(player, PlayerStatus.IN_GAME);
            playerChanceCards.put(player, new Pile<>());
        }

        chanceCards = new Pile<>(Arrays
                .stream(ChanceCard.values())
                .map(Chance::of)
                .collect(Collectors.toList()));
        chanceCards.shuffle();
    }

    /**
     * get the winner(s) of the game.
     * it is players who have the highest amount of money, and they are in the game
     */
    List<Player> getWinners() {
        // get the maximum amount of money of the players
        BigDecimal maxAmount = players.stream().map(bank::getBalance).max(BigDecimal::compareTo).orElseThrow();
        // get the players with the maximum amount of money
        return players.stream().filter(p -> bank.getBalance(p).compareTo(maxAmount) == 0).collect(Collectors.toList());
    }

    /**
     * Game loop.
     * <p>
     * The game loop is composed of a turn for each player.
     * </p>
     */
    public void gameLoop() {
        int maxTurns = 200;
        int turn = 0;
        LOG.info("Game loop started");
        do {
            turn++;
            LOG.info("New turn {}", turn);
            currentTurn = new TurnImpl(this, getCurrentPlayer());
            strategyMap.get(getCurrentPlayer()).playTurn(currentTurn);
            if (!currentTurn.isFinished()) {
                // finish the turn
                currentTurn.endTurn();
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

    void printStatistics() {
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

    List<Integer> getPlayerProperties(Player player) {
        return this.landOwnerMap.keySet().stream().filter(landId -> player.equals(landOwnerMap.get(landId)))
                .collect(Collectors.toList());
    }

    boolean nextPlayer() {
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

    Player getCurrentPlayer() {
        return players.get(currentPlayerId);
    }

    public int getJailPos() {
        List<Land> lands = board.getLands();
        return IntStream.range(0, lands.size()).filter(i -> lands.get(i).getType() == Land.Type.JAIL).findFirst()
                .orElseThrow();
    }

    public int getStartPos() {
        List<Land> lands = board.getLands();
        return IntStream.range(0, lands.size()).filter(i -> lands.get(i).getType() == Land.Type.START).findFirst()
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
        return landOwnerMap.containsKey(pos);
    }

    public void setPlayerPos(Player player, int pos) {
        LOG.info("Player {} moved to {} ({})", player, pos, board.getLands().get(pos).getName());
        if (board.getLands().get(pos).getType() == Land.Type.PROPERTY) {
            if (landOwnerMap.get(pos) != null)
                LOG.info("The {} is owned by {}", board.getLands().get(pos).getName(), landOwnerMap.get(pos));
            else
                LOG.info("The {} is free", board.getLands().get(pos).getName());

        }
        playerPos.put(player, pos);
    }

    public Player getLandOwner(int landId) {
        return landOwnerMap.get(landId);
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
        LOG.info("Player {} has to pay {} tax", player.getName(), amount);
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
        LOG.info("Player {} has to pay {} rent to {}", player.getName(), amount, owner.getName());
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
        if (getBoard().getLand(landId) != property)
            throw new IllegalArgumentException("The landId is not correct");
        LOG.info("Player {} is buying property {}...", player, property);
        if (isOwned(landId)) {
            throw new IllegalStateException("Property is already owned");
        }
        bank.withdraw(player, property.getPrice());
        landOwnerMap.put(landId, player);
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
     * Remove the property from the player.
     *
     * @param player the player who owns the property
     * @param landId the id of the property
     */
    void removePlayerProperty(Player player, Integer landId) {
        assert landOwnerMap.get(landId).equals(player);
        landOwnerMap.remove(landId);
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
    public void doContract(Player player, int landId, Property property, BigDecimal amount) throws BankException {
        LOG.info("Player {} is contracting {} for {}", player, property, amount);
        if (board.getLand(landId) != property)
            throw new IllegalStateException("Property don't belong to the player");
        bank.addMoney(player, amount);
        Player p = landOwnerMap.remove(landId);
        if (p != player)
            throw new IllegalStateException("Property is not owned by the player");
        LOG.info("Player {} sold property {}", player, property);
    }

    public int foundLandByName(String name) {
        for (int i = 0; i < board.getLands().size(); i++) {
            if (board.getLands().get(i).getName().equals(name)) {
                return i;
            }
        }
        throw new NoSuchElementException("No land found with name " + name);
    }

    public List<Integer> foundLandsByColor(Property.Color color) {
        List<Integer> lands = new ArrayList<>();
        for (int i = 0; i < board.getLands().size(); i++) {
            Land land = board.getLands().get(i);
            if (land instanceof Property) {
                Property property = (Property) land;
                if (property.getColor() == color) {
                    lands.add(i);
                }
            }
        }
        return lands;
    }

    public Chance popChanceCard() {
        LOG.info("Popping chance card");
        return chanceCards.pop();
    }

    public void returnChanceCard(Chance x) {
        LOG.info("Returning chance card {}", x);
        chanceCards.returnCard(x);
    }

    public void setPlayerLost(Player player) {
        LOG.info("Player {} has lost the game", player);
        setPlayerStatus(player, Game.PlayerStatus.OUT_OF_GAME);
        removeBelongsFromPlayer(player);
    }

    void removeBelongsFromPlayer(Player player) {
        getPlayerProperties(player).forEach(landId -> removePlayerProperty(player, landId));
    }

    public void leaveJail(Player player) {
        LOG.info("Player {} is leaving jail", player);
        if (getPlayerStatus(player) != PlayerStatus.IN_JAIL)
            throw new IllegalStateException("Player is not in jail");
        setPlayerStatus(player, PlayerStatus.IN_GAME);
    }

    public List<Land.Entry<Property>> getProperties(Player player) {
        return landOwnerMap.keySet()
                .stream()
                .filter(landId -> landOwnerMap.get(landId).equals(player))
                .map(landId -> new Land.Entry<>(landId, (Property) board.getLand(landId)))
                .collect(Collectors.toList());
    }

    public void returnChanceCardToPlayer(Player player, ActionCard card) {
        LOG.info("Returning chance card {} to player {}", card, player);
        playerChanceCards.getOrDefault(player, new Pile<>()).add(card);
    }

    public List<ActionCard> getPlayerChanceCards(Player player) {
        LOG.info("Getting chance cards from player {}", player);
        Pile<ActionCard> chancePile = playerChanceCards.getOrDefault(player, new Pile<>());
        return chancePile.popAll().stream()
                .peek(x -> LOG.info("Got card {}", x))
                .collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return ImmutableList.copyOf(players);
    }

    public void ownProperty(Player player, int landId, Property property) {
        if (isOwned(landId)) {
            LOG.info("Player {} owns property {} from {}", player, property, landOwnerMap.get(landId));
            removePlayerProperty(landOwnerMap.get(landId), landId);
            landOwnerMap.put(landId, player);
        } else {
            LOG.info("Player {} owns free property {}", player, property);
            landOwnerMap.put(landId, player);
        }
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

        public boolean isFinal() {
            return isFinal;
        }
    }
}
