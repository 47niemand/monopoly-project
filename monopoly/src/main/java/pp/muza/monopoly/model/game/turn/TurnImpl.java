package pp.muza.monopoly.model.game.turn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.game.BaseGame;
import pp.muza.monopoly.model.pieces.lands.PropertyColor;

import java.util.List;

public class TurnImpl extends BaseTurn implements Turn {

    private static final Logger LOG = LoggerFactory.getLogger(TurnImpl.class);

    private final Game game;

    protected TurnImpl(Game game, Player player, int turnNumber) {
        super((BaseGame) game, player, turnNumber);
        this.game = game;
    }

    public static Turn of(Game game, Player player, int turnNumber) {
        return new TurnImpl(game, player, turnNumber);
    }

    @Override
    public Fortune popFortuneCard() {
        return game.popFortuneCard();
    }

    @Override
    public void setPlayerInJail() throws TurnException {
        try {
            game.setPlayerInJail(player);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public List<Land> moveTo(int position) throws TurnException {
        try {
            return game.moveTo(player, position);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void buyProperty(int landId) throws BankException, TurnException {
        try {
            game.buyProperty(player, landId);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void leaveJail() throws TurnException {
        try {
            game.leaveJail(player);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }


    @Override
    public void doContract(int landId) throws BankException, TurnException {
        try {
            game.doContract(player, landId);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public int foundProperty(Asset asset) {
        return game.findProperty(asset);
    }

    @Override
    public List<Integer> foundLandsByColor(PropertyColor color) {
        return game.findLandsByColor(color);
    }

    @Override
    public void sendCard(Player to, ActionCard actionCard) throws TurnException {
        try {
            game.sendCard(this.player, to, actionCard);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void birthdayParty() {
        // TODO implement

    }

    @Override
    public void tradeProperty(Player seller, int landId) throws BankException, TurnException {
        try {
            game.tradeProperty(this.player, seller, landId);
        } catch (GameException e) {
            throw new TurnException(e);
        }
    }

    @Override
    public void playerTurnStarted() {

    }

    @Override
    public int getRent(int position) {
        return game.getRent(position);
    }

    @Override
    public void income(int value) throws BankException {
        game.income(player, value);
    }


    @Override
    public PlayerStatus getPlayerStatus() {
        return game.getPlayerStatus(player);
    }

    @Override
    public int nextPosition(int distance) {
        return game.nextPosition(player, distance);
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
    public int getJailFine() {
        return game.getJailFine();
    }

    @Override
    public List<IndexedEntry<Property>> getProperties() {
        return game.getProperties(player);
    }

    @Override
    public int getStartPos() {
        return game.getStartPosition();
    }


    @Override
    public List<IndexedEntry<Property>> getAllProperties() {
        return game.getAllProperties();
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return game.getPlayerStatus(player);
    }

    @Override
    public List<IndexedEntry<Property>> getFreeProperties() {
        return game.getFreeProperties();
    }

    @Override
    public void withdraw(int value) throws BankException {
        game.withdraw(player, value);
    }

    @Override
    public void endTurn() {
        try {
            finishTurn();
        } catch (TurnException e) {
            LOG.error("Error while finishing turn", e);
        }
    }
}
