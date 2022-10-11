package pp.muza.monopoly.model.game;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.consts.Meta;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
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
import pp.muza.monopoly.model.pieces.actions.Gift;

/**
 * The Turn interface implementation. The class is not intended to be used directly.
 *
 * @author dmytromuza
 */
public abstract class BaseTurnImpl implements Turn {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTurnImpl.class);


    protected abstract Game game();

    protected abstract Player player();

    @Override
    public Fortune popFortuneCard() {
        return game().takeFortuneCard();
    }

    @Override
    public void setPlayerInJail() throws TurnException {
        try {
            game().setPlayerInJail(player());
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public List<Land> moveTo(int position) throws TurnException {
        try {
            return game().moveTo(player(), position);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void buyProperty(int position) throws BankException, TurnException {
        try {
            game().buyProperty(player(), position);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void leaveJail() throws TurnException {
        try {
            game().leaveJail(player());
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void doContract(int position) throws BankException, TurnException {
        try {
            game().doContract(player(), position);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public int foundProperty(Asset asset) {
        return game().findProperty(asset);
    }

    @Override
    public List<Integer> foundLandsByColor(PropertyColor color) {
        return game().findLandsByColor(color);
    }

    @Override
    public void sendCard(Player to, ActionCard actionCard) throws TurnException {
        try {
            game().sendCard(this.player(), to, actionCard);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void doBirthdayParty() throws TurnException {
        LOG.info("Birthday party for {}", player());
        holdTurn();
        Player player = getPlayer();
        for (Player guest : game().getPlayers()) {
            if (guest != player && !getPlayerStatus(guest).isFinal()) {
                try {
                    sendCard(guest, Gift.of(Meta.BIRTHDAY_GIFT_AMOUNT, player));
                } catch (TurnException e) {
                    LOG.error("Error while sending card to player {}", guest, e);
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public void holdTurn() throws TurnException {
        try {
            game().holdTurn(this);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void tradeProperty(Player seller, int position) throws BankException, TurnException {
        try {
            game().tradeProperty(this.player(), seller, position);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public int getRent(int position) {
        return game().getRent(position);
    }

    @Override
    public void income(int value) throws BankException {
        game().income(player(), value);
    }

    @Override
    public PlayerStatus getPlayerStatus() {
        return game().getPlayerStatus(player());
    }

    @Override
    public int nextPosition(int distance) {
        return game().nextPosition(player(), distance);
    }

    @Override
    public Land getLand(int position) {
        return game().getLand(position);
    }

    @Override
    public Player getPropertyOwner(int position) {
        return game().getPropertyOwner(position);
    }

    @Override
    public int getJailFine() {
        return game().getJailFine();
    }

    @Override
    public List<IndexedEntry<Property>> getProperties() {
        return game().getProperties(player());
    }

    @Override
    public int getStartPos() {
        return game().getStartPosition();
    }

    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        return game().getAllProperties();
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return game().getPlayerStatus(player);
    }

    @Override
    public List<IndexedEntry<Property>> getFreeProperties() {
        return game().getFreeProperties();
    }

    @Override
    public void withdraw(int value) throws BankException {
        game().withdraw(player(), value);
    }

    @Override
    public Player getPlayer() {
        return player();
    }

    @Override
    public void endTurn() throws TurnException {
        try {
            game().endTurn(this);
        } catch (GameException e) {
            LOG.error("Error while ending turn", e);
            throw new TurnException(e);
        }
    }

    @Override
    public List<Player> getPlayers() {
        return game().getPlayers();
    }
}
