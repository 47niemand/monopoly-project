package pp.muza.monopoly.model.game;

import org.junit.jupiter.api.Test;
import pp.muza.monopoly.model.actions.cards.chance.Chance;
import pp.muza.monopoly.model.actions.cards.chance.ChanceCard;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.player.Player;

import java.util.ArrayList;
import java.util.List;

class GameTest {

    @Test
    void gameLoop1() {

        List<Player> players = new ArrayList<>();
        players.add(new Player("@Player1"));
        players.add(new Player("@Player2"));
        players.add(new Player("@Player3"));
        players.add(new Player("@Player4"));

        Board<Land> board = BoardUtils.defaultBoard();
        Bank bank = new Bank();
        Game game = new Game(board, bank, players);

        game.returnChanceCardToPlayer(game.getPlayers().get(0), Chance.of(ChanceCard.GET_OUT_OF_JAIL_FREE));

        game.gameLoop();


    }

    @Test
    void gameLoop2() {

        List<Player> players = new ArrayList<>();
        players.add(new Player("@Player1"));
        players.add(new Player("@Player2"));
        players.add(new Player("@Player3"));
        players.add(new Player("@Player4"));

        Board<Land> board = BoardUtils.defaultBoard();
        Bank bank = new Bank();
        Game game = new Game(board, bank, players);

        game.returnChanceCardToPlayer(game.getPlayers().get(0), Chance.of(ChanceCard.ADVANCE_TO_BLUE_OR_ORANGE));

        game.gameLoop();
    }

    @Test
    void gameLoop3() {

        List<Player> players = new ArrayList<>();
        players.add(new Player("@Player1"));
        players.add(new Player("@Player2"));
        players.add(new Player("@Player3"));
        players.add(new Player("@Player4"));

        Board<Land> board = BoardUtils.defaultBoard();
        Bank bank = new Bank();
        Game game = new Game(board, bank, players);

        game.returnChanceCardToPlayer(game.getPlayers().get(0), Chance.of(ChanceCard.GIVE_THIS_CARD_TO_A_PLAYER_2));

        game.gameLoop();

    }



    @Test
    void gameLoop4() {

        List<Player> players = new ArrayList<>();
        players.add(new Player("@Player1"));
        players.add(new Player("@Player2"));

        Board<Land> board = BoardUtils.defaultBoard();
        Bank bank = new Bank();
        Game game = new Game(board, bank, players);

        game.returnChanceCardToPlayer(game.getPlayers().get(0), Chance.of(ChanceCard.GIVE_THIS_CARD_TO_A_PLAYER_3));

        game.gameLoop();

    }

}