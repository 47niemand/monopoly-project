package pp.muza.monopoly.model.turn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Turn;

/**
 * The turn of the game implementation. This class implements the Turn interface.
 */
public class TurnImpl implements Turn {

    private static final Logger LOG = LoggerFactory.getLogger(TurnImpl.class);

    private final Game game;
    private final Player player;
    private final List<ActionCard> usedCards = new ArrayList<>();
    private boolean finished;

    public TurnImpl(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public List<ActionCard> getActiveActionCards() {
        List<ActionCard> result = game.getActiveActionCards(player);
        LOG.info("{}: active action cards: {}", player.getName(),
                result.stream().map(ActionCard::getName).collect(Collectors.toList()));
        return result;
    }

    @Override
    public boolean playCard(ActionCard actionCard) throws TurnException {
        if (isFinished()) {
            throw new TurnException("The turn is finished.");
        }
        LOG.info("{}: playing card {}", player.getName(), actionCard.getName());
        boolean result = game.playCard(this, actionCard);
        LOG.debug("Card {} played: {}", actionCard, result);
        if (usedCards.remove(actionCard)) {
            LOG.debug("{}: card {} was already used", player.getName(), actionCard);
            // it is not an error if the card was already used.
        }
        if (result) {
            usedCards.add(actionCard);
        }
        return result;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int nextPosition(int distance) {
        return game.getNextPosition(player, distance);
    }

    @Override
    public Land getLand(int position) {
        return game.getLand(position);
    }

    @Override
    public Player getPropertyOwner(int position) {
        return game.getPropertyOwner(position);
    }

    @Override
    public Fortune popFortuneCard() {
        return game.popFortuneCard();
    }

    @Override
    public void setPlayerInJail() {
        game.setPlayerInJail(player);
    }

    @Override
    public PlayerStatus getStatus() {
        return game.getStatus(player);
    }

    @Override
    public BigDecimal getJailFine() {
        return game.getJailFine();
    }

    @Override
    public List<Land> moveTo(int position) {
        return game.moveTo(player, position);
    }

    @Override
    public List<IndexedEntry<Property>> getProperties() {
        return game.getProperties(player);
    }

    @Override
    public void buyProperty(int landId) throws BankException, TurnException {
        game.buyProperty(player, landId);
    }

    @Override
    public BigDecimal getRent(int landId) {
        return game.getRent(landId);
    }

    @Override
    public void income(BigDecimal amount) throws BankException {
        game.income(player, amount);
    }

    @Override
    public Map<Integer, Player> getPropertyOwners() {
        return game.getPropertyOwners();
    }


    @Override
    public void payTax(BigDecimal amount) throws BankException {
        game.payTax(player, amount);
    }

    @Override
    public void leaveJail() throws TurnException {
        game.leaveJail(player);
    }

    @Override
    public void endTurn() throws TurnException {
        if (finished) {
            LOG.warn("Turn already finished");
            throw new TurnException("Turn already finished");
        }
        LOG.info("Finishing turn for player {}", player.getName());
        LOG.info("Used cards: {}",
                usedCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        game.endTurn(player);
        finished = true;
    }

    @Override
    public void doContract(int landId) throws BankException, TurnException {
        game.doContract(player, landId);
    }

    @Override
    public int getStartPos() {
        return game.getStartPosition();
    }

    @Override
    public int foundProperty(Property.Asset asset) {
        return game.findProperty(asset);
    }

    @Override
    public List<Integer> foundLandsByColor(Property.Color color) {
        return game.findLandsByColor(color);
    }

    @Override
    public List<Player> getPlayers() {
        return game.getPlayers().stream().filter(p -> !getPlayerStatus(p).isFinished()).collect(Collectors.toList());
    }

    @Override
    public void sendCard(Player player, ActionCard actionCard) {
        game.sendCard(player, actionCard);
    }

    @Override
    public List<IndexedEntry<Property>> getFreeProperties() {
        return getAllProperties().stream()
                .filter(x -> game.getPropertyOwner(x.getIndex()) == null)
                .collect(Collectors.toList());
    }

    @Override
    public void withdraw(BigDecimal amount) throws BankException {
        game.withdraw(player, amount);
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        return game.getAllProperties();
    }

    @Override
    public void birthdayParty() {
        game.birthdayParty(player);
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return game.getStatus(player);
    }

    @Override
    public void tradeProperty(Player salePlayer, int landId) throws BankException, TurnException {
        game.tradeProperty(player, salePlayer, landId);
    }

    @Override
    public void playerTurnStarted() {
        game.playerTurnStarted(player);
    }

    @Override
    public String toString() {
        return "Turn(game=" + this.game
                + ", player=" + this.getPlayer().getName()
                + ", usedCards=" + this.usedCards.stream().map(ActionCard::getName).collect(Collectors.toList())
                + ", finished=" + this.isFinished() + ")";
    }
}
