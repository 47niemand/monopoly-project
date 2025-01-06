package pp.muza.monopoly.app.print;

import org.junit.jupiter.api.Test;
import pp.muza.monopoly.model.BoardLayout;

public class TestPrintBoard {

    @Test
    public void testPrintBoard() {

        String board = PrintBoard.printBoard(BoardLayout.defaultBoard());
        System.out.println(board);

    }
}
