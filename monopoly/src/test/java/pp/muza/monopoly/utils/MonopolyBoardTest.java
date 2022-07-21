package pp.muza.monopoly.utils;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.bank.BankImpl;
import pp.muza.monopoly.model.game.GameImpl;
import pp.muza.monopoly.strategy.ObedientStrategy;

import static org.junit.jupiter.api.Assertions.*;

class MonopolyBoardTest {

    @Test
    void defaultBoard() {
        Board board = MonopolyBoard.defaultBoard();
        assertEquals(0, board.getStartPosition(), "Start position should be 0");
        assertEquals(Land.Type.START, board.getLands().get(board.getStartPosition()).getType(), "Start should be the first land");
    }
}