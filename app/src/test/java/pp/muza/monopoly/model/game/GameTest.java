package pp.muza.monopoly.model.game;

import org.junit.jupiter.api.Test;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.lands.BoardHelper;
import pp.muza.monopoly.model.player.strategy.DefaultPlayer;

import java.util.ArrayList;
import java.util.List;

class GameTest {

    @Test
    void gameLoop() {

        List<Player> players = new ArrayList<>();
        players.add(new DefaultPlayer("Player 1"));
        players.add(new DefaultPlayer("Player 2"));
        players.add(new DefaultPlayer("Player 3"));

        Board<Land> board = BoardHelper.defaultBoard();
        Bank bank = new Bank();
        Game game = new Game(board, bank, players);
        game.gameLoop();
    }


}