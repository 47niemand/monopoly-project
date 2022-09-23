package pp.muza.monopoly.model.game;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.BoardLayout;
import pp.muza.monopoly.model.ChancePile;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.PlayGame;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.bank.BankImpl;

public class Monopoly implements PlayGame {

    final BaseGame baseGame;
    private final Bank bank = new BankImpl();
    private final Board board = BoardLayout.defaultBoard();
    private final List<Fortune> fortunes = ChancePile.defaultPile();
    private final List<Player> players;

    public Monopoly(List<Player> players) {
        this.players = ImmutableList.copyOf(players);
        Collections.shuffle(fortunes);
        this.baseGame = new BaseGame(bank, board, fortunes, this.players) {
        };
    }

    @Override
    public boolean isGameInProgress() {
        return baseGame.isGameInProgress();
    }

    @Override
    public void start() throws GameException {
        baseGame.start();
    }

    @Override
    public PlayTurn getTurn() throws GameException {
        return baseGame.getTurn();
    }

    @Override
    public List<Player> getPlayers() {
        return baseGame.getPlayers();
    }

    @Override
    public PlayerInfo getPlayerInfo(Player player) {
        return baseGame.getPlayerInfo(player);
    }

    @Override
    public List<ActionCard> getActiveCards(Player player) {
        return baseGame.getActiveCards(player);
    }

    @Override
    public List<ActionCard> getCards(Player player) {
        return baseGame.getCards(player);
    }

    @Override
    public Map<Integer, Player> getPropertyOwners() {
        return baseGame.getPropertyOwners();
    }

    @Override
    public Board getBoard() {
        return baseGame.getBoard();
    }

    @Override
    public PlayerStatus getPlayerStatus(Player player) {
        return baseGame.playerData(player).getStatus();
    }

    @Override
    public int getBalance(Player player) {
        return baseGame.getBank().getBalance(player);
    }

    @Override
    public int getTurnNumber() {
        return baseGame.getTurnNumber();
    }
}
