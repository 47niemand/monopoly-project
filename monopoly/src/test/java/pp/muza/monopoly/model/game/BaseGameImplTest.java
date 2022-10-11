package pp.muza.monopoly.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;

class BaseGameImplTest {

    @Test
    void moveTo() throws GameException {

        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.baseGame.playerData(player1).setPosition(1);
        // board should have at least 23 lands
        assert game.getBoard().size() >= 23;

        // stay at the same position
        List<Land> l = game.baseGame.getGame().moveTo(player1, 1);
        assertEquals(1, game.baseGame.playerData(player1).getPosition());
        assertEquals(0, l.size());

        // move to the next position
        l = game.baseGame.getGame().moveTo(player1, 23);
        assertEquals(23, game.baseGame.playerData(player1).getPosition());
        assertEquals(22, l.size());
    }

}