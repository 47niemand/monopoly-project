package pp.muza.monopoly.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.BoardLayout;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.pieces.lands.LandType;

import java.util.LinkedHashMap;
import java.util.Map;

class BoardLayoutTest {

    @Test
    void defaultBoard() {
        Board board = BoardLayout.defaultBoard();
        Assertions.assertEquals(0, board.getStartPosition(), "Start position should be 0");
        Assertions.assertEquals(LandType.START, board.getLands().get(board.getStartPosition()).getType(), "Start should be the first land");
    }

    @Test
    void printBoard() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Map<String, Land> map = new LinkedHashMap<>();
        Board board =  BoardLayout.defaultBoard();
        for (int i = 0; i <  board.getLands().size(); i++) {
            Land l = board.getLands().get(i);
            map.put("" + i, l);
        }
        System.out.println(mapper.writeValueAsString(map));

    }
}