package pp.muza.monopoly.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.pieces.lands.LandType;

class MonopolyBoardTest {

    @Test
    void defaultBoard() {
        Board board = MonopolyBoard.defaultBoard();
        Assertions.assertEquals(0, board.getStartPosition(), "Start position should be 0");
        Assertions.assertEquals(LandType.START, board.getLands().get(board.getStartPosition()).getType(), "Start should be the first land");
    }
}