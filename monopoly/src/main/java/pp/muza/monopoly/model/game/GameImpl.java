package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Asset;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.PropertyColor;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.lands.Jail;
import pp.muza.monopoly.model.pieces.lands.LandType;

/**
 * The Game interface implementation.
 * Marked as abstract to prevent instantiation of this class.
 */
public abstract class GameImpl implements Game {

    static final Logger LOG = LoggerFactory.getLogger(GameImpl.class);

    protected abstract BaseGame baseGame();

    private void checkPlayerInGame(Player player) throws GameException {
        if (baseGame().playerData(player).getStatus().isFinal()) {
            LOG.error("Player {} is not in game", player);
            throw new GameException("Player is not in game");
        }
    }

    @Override
    public int getJailPosition() {
        return IntStream.range(0, baseGame().getBoard().getLands().size())
                .filter(i -> baseGame().getBoard().getLands().get(i).getType() == LandType.JAIL)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No jail found"));
    }

    @Override
    public Fortune takeFortuneCard() {
        return baseGame().takeFortuneCard();
    }

    @Override
    public void setPlayerInJail(Player player) throws GameException {
        checkPlayerInGame(player);
        PlayerData playerData = baseGame().playerData(player);
        playerData.setStatus(PlayerStatus.IN_JAIL);
        playerData.setPosition(getJailPosition());
    }

    @Override
    public List<Land> moveTo(Player player, int position) throws GameException {
        checkPlayerInGame(player);
        PlayerData playerData = baseGame().playerData(player);
        List<Integer> path = baseGame().getBoard().getPathTo(playerData.getPosition(), position);
        List<Land> lands = baseGame().getBoard().getLands(path);
        playerData.setPosition(position);
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
        baseGame().getBank().withdraw(player, price);
        baseGame().setPropertyOwner(landId, player);
    }

    @Override
    public void leaveJail(Player player) throws GameException {
        if (getPlayerStatus(player) != PlayerStatus.IN_JAIL) {
            throw new GameException("Player is not in jail");
        }
        baseGame().playerData(player).setStatus(PlayerStatus.IN_GAME);
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
        baseGame().getBank().deposit(player, value);
        baseGame().propertyOwnerRemove(landId);
    }

    @Override
    public int findProperty(Asset asset) {
        for (int i = 0; i < baseGame().getBoard().getLands().size(); i++) {
            Land land = baseGame().getBoard().getLands().get(i);
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
        for (int i = 0; i < baseGame().getBoard().getLands().size(); i++) {
            Land land = baseGame().getBoard().getLands().get(i);
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
        LOG.info("Player {} is sending card '{}' to {}", sender.getName(), actionCard.getName(), to.getName());
        checkPlayerInGame(to);
        baseGame().playerData(to).addCard(actionCard);
    }

    @Override
    public void tradeProperty(Player buyer, Player seller, int landId) throws BankException, GameException {
        Property property = (Property) baseGame().getBoard().getLand(landId);
        if (getPropertyOwner(landId) != seller) {
            throw new GameException("Land is not owned by " + seller.getName());
        }
        if (seller == buyer) {
            throw new GameException("You can't trade with yourself");
        }
        int price = property.getPrice();
        baseGame().getBank().withdraw(buyer, price);
        baseGame().getBank().deposit(seller, price);
        baseGame().setPropertyOwner(landId, buyer);
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
        baseGame().getBank().deposit(player, value);
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return baseGame().playerData(player).getStatus();
    }

    @Override
    public int nextPosition(Player player, int distance) {
        return baseGame().getBoard().getDestination(baseGame().playerData(player).getPosition(), distance);
    }

    @Override
    public Land getLand(int position) {
        return baseGame().getBoard().getLand(position);
    }

    @Override
    public Player getPropertyOwner(int position) {
        return baseGame().getPropertyOwner(position);
    }

    @Override
    public int getJailFine() {
        return baseGame().getBoard().getLands().stream()
                .filter(land -> land.getType() == LandType.JAIL)
                .map(x -> ((Jail) x).getFine())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No jail found"));
    }

    @Override
    public List<IndexedEntry<Property>> getProperties(Player player) {
        return baseGame().belongings(player).stream()
                .map(x -> new IndexedEntry<>(x, (Property) baseGame().getBoard().getLand(x)))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public int getStartPosition() {
        return baseGame().getBoard().getStartPosition();
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        List<Land> lands = baseGame().getBoard().getLands();
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
        baseGame().getBank().withdraw(player, value);
    }

    @Override
    public List<Player> getPlayers() {
        return baseGame().getPlayers();
    }

    @Override
    public void endTurn(Turn turn) throws GameException {
        baseGame().finishTurn(turn);
    }

    @Override
    public void holdTurn(TurnImpl turn) throws GameException {
        baseGame().holdTurn(turn);
    }


}
