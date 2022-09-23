package pp.muza.monopoly.model.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.pieces.lands.Jail;
import pp.muza.monopoly.model.pieces.lands.LandType;
import pp.muza.monopoly.model.pieces.lands.PropertyColor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameImpl extends BaseGame implements Game {

    static final Logger LOG = LoggerFactory.getLogger(GameImpl.class);

    public GameImpl(Bank bank, Board board, List<Fortune> fortuneCards, List<Player> players) {
        super(bank, board, fortuneCards, players);
    }

    public static BaseGame create(Bank bank, Board board, List<Fortune> fortuneCards, List<Player> players) {
        return new GameImpl(bank, board, fortuneCards, players);
    }

    private void checkPlayerInGame(Player player) throws GameException {
        if (playerContext(player).getStatus().isFinal()) {
            throw new GameException("Player is not in game");
        }
    }

    @Override
    public Fortune popFortuneCard() {
        return fortuneCards.removeFirst();
    }

    @Override
    public void setPlayerInJail(Player player) throws GameException {
        checkPlayerInGame(player);
        PlayerContext playerContext = playerContext(player);
        playerContext.setStatus(PlayerStatus.IN_JAIL);
        playerContext.setPosition(getJailPosition());
    }

    private int getJailPosition() {
        return IntStream.range(0, getBoard().getLands().size())
                .filter(i -> getBoard().getLands().get(i).getType() == LandType.JAIL)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No jail found"));
    }

    @Override
    public List<Land> moveTo(Player player, int position) throws GameException {
        checkPlayerInGame(player);
        PlayerContext playerContext = playerContext(player);
        List<Integer> path = getBoard().getPathTo(playerContext.getPosition(), position);
        List<Land> lands = getBoard().getLands(path);
        playerContext.setPosition(position);
        return lands;
    }

    @Override
    public void buyProperty(Player player, int landId) throws GameException, BankException {
        checkPlayerInGame(player);
        Property property = (Property) getLand(landId);
        int price = property.getPrice();
        if (getPropertyOwner(landId) != null) {
            throw new GameException("Land is already owned");
        }
        getBank().withdraw(player, price);
        setPropertyOwner(landId, player);
    }

    @Override
    public void leaveJail(Player player) throws GameException {
        if (getPlayerStatus(player) != PlayerStatus.IN_JAIL) {
            throw new GameException("Player is not in jail");
        }
        playerContext(player).setStatus(PlayerStatus.IN_GAME);
    }


    @Override
    public void doContract(Player player, int landId) throws BankException, GameException {
        checkPlayerInGame(player);
        Property property = (Property) getLand(landId);
        if (getPropertyOwner(landId) != player) {
            throw new GameException("Land is not owned by Player");
        }
        LOG.info("Player {} is contracting property {} ({})", player.getName(), landId, property.getName());
        int value = property.getPrice();
        getBank().deposit(player, value);
        propertyOwnerRemove(landId);
    }

    @Override
    public int findProperty(Asset asset) {
        for (int i = 0; i < getBoard().getLands().size(); i++) {
            Land land = getBoard().getLands().get(i);
            if (land.getType() == LandType.PROPERTY) {
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
    public List<Integer> findLandsByColor(PropertyColor color) {
        List<Integer> lands = new ArrayList<>();
        for (int i = 0; i < getBoard().getLands().size(); i++) {
            Land land = getBoard().getLands().get(i);
            if (land.getType() == LandType.PROPERTY) {
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
    public void sendCard(Player sender, Player to, ActionCard actionCard) throws GameException {
        LOG.info("Player {} is sending card '{}' to {}", sender.getName(), actionCard, to.getName());
        checkPlayerInGame(to);
        playerContext(to).addCard(actionCard);
    }

    @Override
    public void tradeProperty(Player buyer, Player seller, int landId) throws BankException, GameException {
        Property property = (Property) getBoard().getLand(landId);
        if (getPropertyOwner(landId) != seller) {
            throw new GameException("Land is not owned by " + seller.getName());
        }
        if (seller == buyer) {
            throw new GameException("You can't trade with yourself");
        }
        int price = property.getPrice();
        getBank().withdraw(buyer, price);
        getBank().deposit(seller, price);
        setPropertyOwner(landId, buyer);
    }

    @Override
    public int getRent(int position) {
        int rent;
        Property property = (Property) getLand(position);
        Player owner = getPropertyOwner(position);
        if (owner == null) {
            rent = 0;
            LOG.info("Land {} is not owned", position);
        } else {
            List<Integer> sameColorLands = findLandsByColor(property.getColor());
            boolean sameColor = sameColorLands.stream()
                    .map(this::getPropertyOwner)
                    .allMatch(x -> x == owner);
            if (sameColor) {
                // double rent if the player owns all properties of the same color
                rent = property.getPrice() * 2;
                LOG.info("Player {} owns all properties of the same color {}, so the owner gets double rent: {}", owner.getName(), property.getColor(), rent);
            } else {
                rent = property.getPrice();
                LOG.info("Player {} owns property {}, so the owner gets rent: {}", owner.getName(), property.getName(), rent);
            }
        }
        return rent;
    }

    @Override
    public void income(Player player, int value) throws BankException {
        getBank().deposit(player, value);
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return playerContext(player).getStatus();
    }

    @Override
    public int nextPosition(Player player, int distance) {
        return getBoard().getDestination(playerContext(player).getPosition(), distance);
    }

    @Override
    public Land getLand(int position) {
        return getBoard().getLand(position);
    }

    @Override
    public Player getPropertyOwner(int position) {
        return propertyOwners.get(position);
    }

    @Override
    public int getJailFine() {
        return getBoard().getLands().stream()
                .filter(land -> land.getType() == LandType.JAIL)
                .map(x -> ((Jail) x).getFine())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No jail found"));
    }

    @Override
    public List<IndexedEntry<Property>> getProperties(Player player) {
        return belongings(player);
    }

    @Override
    public int getStartPosition() {
        return getBoard().getStartPosition();
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        List<Land> lands = getBoard().getLands();
        List<IndexedEntry<Property>> properties = new ArrayList<>();
        for (int i = 0; i < lands.size(); i++) {
            Land land = lands.get(i);
            if (land.getType() == LandType.PROPERTY) {
                Property property = (Property) land;
                properties.add(new IndexedEntry<>(i, property));
            }
        }
        return properties;
    }

    @Override
    public List<IndexedEntry<Property>> getFreeProperties() {
        return getAllProperties().stream()
                .filter(x -> getPropertyOwner(x.getIndex()) == null)
                .collect(Collectors.toList());
    }

    @Override
    public void withdraw(Player player, int value) throws BankException {
        getBank().withdraw(player, value);
    }
}
