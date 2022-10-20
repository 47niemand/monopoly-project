package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.consts.RuleOption;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.GameError;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Asset;
import pp.muza.monopoly.model.Biding;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Offer;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.PropertyColor;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.lands.Jail;
import pp.muza.monopoly.model.pieces.lands.LandType;

/**
 * The Game interface implementation.
 *
 * @author dmytromuza
 */
public class GameImpl implements Game {

    static final Logger LOG = LoggerFactory.getLogger(GameImpl.class);

    private final BaseGame baseGame;

    protected GameImpl(BaseGame baseGame) {
        this.baseGame = baseGame;
    }

    private void checkPlayerInGame(Player player) throws GameException {
        if (baseGame.playerData(player).getStatus().isFinal()) {
            LOG.error("Player {} is not in game", player);
            throw new GameException(GameError.PLAYER_IS_NOT_IN_GAME);
        }
    }

    private void checkLandIsProperty(int position) throws GameException {
        if (baseGame.getBoard().getLand(position).getType() != LandType.PROPERTY) {
            LOG.error("Land at position {} is not property", position);
            throw new GameException(GameError.LAND_IS_NOT_PROPERTY);
        }
    }

    @Override
    public int getJailPosition() {
        return IntStream.range(0, baseGame.getBoard().getLands().size())
                .filter(i -> baseGame.getBoard().getLands().get(i).getType() == LandType.JAIL)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No jail found"));
    }

    @Override
    public Fortune takeFortuneCard() {
        return baseGame.takeFortuneCard();
    }

    @Override
    public void setPlayerInJail(Player player) throws GameException {
        checkPlayerInGame(player);
        PlayerData playerData = baseGame.playerData(player);
        playerData.setStatus(PlayerStatus.IN_JAIL);
        playerData.setPosition(getJailPosition());
    }

    @Override
    public List<Land> moveTo(Player player, int position) throws GameException {
        checkPlayerInGame(player);
        PlayerData playerData = baseGame.playerData(player);
        List<Integer> path = baseGame.getBoard().getPathTo(playerData.getPosition(), position);
        List<Land> lands = baseGame.getBoard().getLands(path);
        playerData.setPosition(position);
        return lands;
    }

    @Override
    public void buyProperty(Player player, int position) throws GameException, BankException {
        checkPlayerInGame(player);
        checkLandIsProperty(position);
        Property property = (Property) getLand(position);
        int price = property.getPrice();
        if (getPropertyOwner(position) != null) {
            throw new GameException(GameError.LAND_IS_ALREADY_OWNED);
        }
        baseGame.getBank().withdraw(player, price);
        baseGame.setPropertyOwner(position, player);
    }

    @Override
    public void leaveJail(Player player) throws GameException {
        if (getPlayerStatus(player) != PlayerStatus.IN_JAIL) {
            throw new GameException(GameError.PLAYER_IS_NOT_IN_JAIL);
        }
        baseGame.playerData(player).setStatus(PlayerStatus.IN_GAME);
    }

    @Override
    public void doContract(Player player, int position) throws BankException, GameException {
        checkPlayerInGame(player);
        Property property = (Property) getLand(position);
        if (getPropertyOwner(position) != player) {
            throw new GameException(GameError.LAND_IS_NOT_OWNED_BY_PLAYER);
        }
        LOG.info("Player {} is contracting property {} ({})", player, position, property);
        int value = property.getPrice();
        baseGame.getBank().deposit(player, value);
        baseGame.propertyOwnerRemove(position);
    }

    @Override
    public int findProperty(Asset asset) {
        for (int i = 0; i < baseGame.getBoard().getLands().size(); i++) {
            Land land = baseGame.getBoard().getLands().get(i);
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
        for (int i = 0; i < baseGame.getBoard().getLands().size(); i++) {
            Land land = baseGame.getBoard().getLands().get(i);
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
        LOG.info("Player {} is sending card '{}' to {}", sender, actionCard, to);
        checkPlayerInGame(to);
        baseGame.playerData(to).addCard(actionCard);
    }

    @Override
    public void tradeProperty(Player buyer, Player seller, int position) throws BankException, GameException {
        checkLandIsProperty(position);
        Property property = (Property) baseGame.getBoard().getLand(position);
        if (getPropertyOwner(position) != seller) {
            throw new GameException(GameError.LAND_IS_NOT_OWNED_BY_SELLER);
        }
        if (seller == buyer) {
            throw new GameException(GameError.YOU_CAN_T_TRADE_WITH_YOURSELF);
        }
        int price = property.getPrice();
        baseGame.getBank().withdraw(buyer, price);
        try {
            baseGame.getBank().deposit(seller, price);
        } catch (BankException e) {
            throw new UnexpectedErrorException("Error while depositing money to seller " + seller, e);
        }
        baseGame.setPropertyOwner(position, buyer);
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
                LOG.info("Player {} owns all properties of the same color {}, so the owner gets double rent: {}", owner, property.getColor(), rent);
            } else {
                rent = property.getPrice();
                LOG.info("Player {} owns property {}, so the owner gets rent: {}", owner, property, rent);
            }
        }
        return rent;
    }

    @Override
    public void income(Player player, int value) throws BankException {
        baseGame.getBank().deposit(player, value);
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return baseGame.playerData(player).getStatus();
    }

    @Override
    public int nextPosition(Player player, int distance) {
        return baseGame.getBoard().getDestination(baseGame.playerData(player).getPosition(), distance);
    }

    @Override
    public Land getLand(int position) {
        return baseGame.getBoard().getLand(position);
    }

    @Override
    public Player getPropertyOwner(int position) {
        return baseGame.getPropertyOwner(position);
    }

    @Override
    public int getJailFine() {
        return baseGame.getBoard().getLands().stream()
                .filter(land -> land.getType() == LandType.JAIL)
                .map(x -> ((Jail) x).getFine())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No jail found"));
    }

    @Override
    public List<IndexedEntry<Property>> getProperties(Player player) {
        return baseGame.belongings(player).stream()
                .map(x -> new IndexedEntry<>(x, (Property) baseGame.getBoard().getLand(x)))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public int getStartPosition() {
        return baseGame.getBoard().getStartPosition();
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        List<Land> lands = baseGame.getBoard().getLands();
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
        baseGame.getBank().withdraw(player, value);
    }

    @Override
    public List<Player> getPlayers() {
        return baseGame.getPlayers();
    }

    @Override
    public void endTurn(Turn turn) throws GameException {
        baseGame.finishTurn(turn);
    }

    @Override
    public void holdTurn(Turn turn) throws GameException {
        baseGame.holdTurn(turn);
    }

    @Override
    public void auction(Player player, int postion, int price) throws GameException {
        checkPlayerInGame(player);
        checkLandIsProperty(postion);
        if (getPropertyOwner(postion) != player) {
            throw new GameException(GameError.LAND_IS_NOT_OWNED_BY_PLAYER);
        }
        if (price < 1) {
            throw new GameException(GameError.PRICE_MUST_BE_POSITIVE);
        }
    }

    @Override
    public Biding endAuction(Player player, int position) throws GameException {
        PlayerData playerData = baseGame.playerData(player);
        List<ActionCard> bidings = playerData.getCards().stream()
                .filter(c -> c.getAction() == Action.SUBMIT)
                .filter(d -> ((Biding) d).getPosition() == position)
                .collect(Collectors.toList());
        baseGame.playerData(player).removeCards(bidings);
        if (bidings.isEmpty()) {
            LOG.info("No biding found for player {} and position {}", player, position);
        } else {
            LOG.info("Auction for player {} and position {} is finished", player, position);
        }
        return bidings.stream().map(x -> (Biding) x).max(Comparator.comparingInt(Biding::getPrice)).orElse(null);
    }

    @Override
    public String getRuleOptions(RuleOption option) {
        return baseGame.getRuleOptions(option);
    }

    @Override
    public void takeProperty(Player player, int position) throws GameException {
        checkPlayerInGame(player);
        checkLandIsProperty(position);
        if (getPropertyOwner(position) == null) {
            baseGame.setPropertyOwner(position, player);
        } else if (getPropertyOwner(position) == player) {
            LOG.warn("Player {} already owns property {}", player, position);
        } else {
            throw new GameException(GameError.LAND_IS_ALREADY_OWNED);
        }
    }

    @Override
    public void doBid(Player bidder, int position, int price) throws GameException, BankException {
        Player seller = getPropertyOwner(position);
        if (seller == null) {
            throw new GameException(GameError.LAND_IS_NOT_OWNED);
        }
        if (bidder.equals(seller)) {
            LOG.error("Player {} is trying to bid on his own property", bidder);
            throw new GameException(GameError.SELLER_CANT_BID);
        }
        List<ActionCard> cards = baseGame.playerData(seller).getCards();
        Offer auction = cards.stream()
                .filter(c -> c.getAction() == Action.OFFER)
                .map(c -> ((Offer) c))
                .filter(d -> d.getPosition() == position)
                .findFirst().orElse(null);
        if (auction == null) {
            LOG.error("Player {} is trying to bid on property {} which is not on auction", bidder, position);
            throw new GameException(GameError.AUCTION_IS_NOT_IN_PROGRESS);
        }
        if (price > getBalance(bidder)) {
            LOG.error("Player {} doesn't have enough money to bid", bidder);
            throw new BankException(GameError.NOT_ENOUGH_COINS);
        }
        List<Biding> bidings = cards.stream()
                .filter(c -> c.getAction() == Action.SUBMIT)
                .map(c -> ((Biding) c))
                .filter(d -> d.getPosition() == position)
                .collect(Collectors.toList());
        // get max biding
        if (bidings.isEmpty()) {
            if (price < auction.getPrice()) {
                LOG.error("Player {} is trying to bid on property {} with price {} which is lower than auction price {}", bidder, position, price, auction.getPrice());
                throw new GameException(GameError.BID_MUST_BE_GREATER_THAN_THE_CURRENT_PRICE);
            }
        } else {
            Biding maxBiding = bidings.stream().max(Comparator.comparingInt(Biding::getPrice)).orElseThrow(() -> new IllegalStateException("No biding found"));
            if (price <= maxBiding.getPrice()) {
                LOG.error("Player {} is trying to bid on property {} with price {} which is lower than max biding price {}", bidder, position, price, maxBiding.getPrice());
                throw new GameException(GameError.BID_MUST_BE_GREATER_THAN_THE_CURRENT_PRICE);
            }
        }
    }

    @Override
    public void doSale(Player seller, int position, int price, Player buyer) throws GameException, BankException {
        checkPlayerInGame(seller);
        checkPlayerInGame(buyer);
        if (getPropertyOwner(position) != seller) {
            LOG.error("Seller {} doesn't own property {}", seller, position);
            throw new GameException(GameError.LAND_IS_NOT_OWNED_BY_PLAYER);
        }
        if (buyer == seller) {
            LOG.error("Seller {} can't buy his own property {}", seller, position);
            throw new GameException(GameError.YOU_CAN_T_TRADE_WITH_YOURSELF);
        }
        Property property = (Property) baseGame.getBoard().getLand(position);
        assert property != null;
        baseGame.getBank().withdraw(buyer, price);
        baseGame.setPropertyOwner(position, buyer);
        try {
            baseGame.getBank().deposit(seller, price);
        } catch (BankException e) {
            throw new UnexpectedErrorException("Can't deposit money to seller " + seller, e);
        }
    }

    @Override
    public int getBalance(Player player) {
        return baseGame.getBank().getBalance(player);
    }
}
