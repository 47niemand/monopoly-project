package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.PlayGame;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.game.Monopoly;

import static org.junit.jupiter.api.Assertions.*;

class MonopolyTest {

    @Test
    void isGameInProgress() {
    }

    @Test
    void start() throws GameException, TurnException {
        // test if the game starts

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        PlayGame playGame = new Monopoly(ImmutableList.of(player1, player2));

        // act
        assertFalse(playGame.isGameInProgress());
        playGame.start();
        assertTrue(playGame.isGameInProgress());
        assertThrows(GameException.class, playGame::start);
        PlayTurn turn1 = playGame.getTurn();
        turn1.endTurn();
        assertTrue(turn1.isFinished());
        PlayTurn turn2 = playGame.getTurn();
        assertNotSame(turn1, turn2);
        assertNotSame(turn1.getPlayer(), turn2.getPlayer());
        turn2.endTurn();
        assertEquals(PlayerStatus.OUT_OF_GAME, playGame.getPlayerInfo(player1).getStatus());
        assertEquals(PlayerStatus.OUT_OF_GAME, playGame.getPlayerInfo(player2).getStatus());
        assertFalse(playGame.isGameInProgress());
        assertThrows(GameException.class, playGame::start);
    }

    @Test
    void getTurn() {
    }

    @Test
    void getPlayers() {
    }

    @Test
    void getPlayerInfo() {
    }

    @Test
    void getActiveCards() {
    }

    @Test
    void getCards() {
    }

    @Test
    void getPropertyOwners() {
    }

    @Test
    void getBoard() {
    }
}