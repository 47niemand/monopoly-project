package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.pieces.actions.BirthdayParty;
import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.EndTurn;
import pp.muza.monopoly.strategy.ObedientStrategy;

import static org.junit.jupiter.api.Assertions.*;

class TurnImplTest {

    @Test
    void getOutOfJail() throws GameException, TurnException {
        // test if the game starts

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        PlayGame playGame = new Monopoly(ImmutableList.of(player1, player2));

        // act
        assertFalse(playGame.isGameInProgress());
        playGame.start();

        Monopoly monopoly = (Monopoly) playGame;
        monopoly.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);
        monopoly.baseGame.sendCard(player1, monopoly.baseGame.pickFortuneCard(Chance.GET_OUT_OF_JAIL_FREE));
        PlayTurn turn1 = playGame.getTurn();
        while (!turn1.isFinished()) {
            ActionCard card = ObedientStrategy.getInstance().playTurn(turn1.getTurnInfo());
            turn1.playCard(card);
        }
        assertEquals(PlayerStatus.IN_GAME, playGame.getPlayerInfo(player1).getStatus());
    }


    @Test
    void leaveJail() throws GameException, TurnException {
        // test if the game starts

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        PlayGame playGame = new Monopoly(ImmutableList.of(player1, player2));

        // act
        assertFalse(playGame.isGameInProgress());
        playGame.start();

        Monopoly monopoly = (Monopoly) playGame;
        monopoly.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);

        PlayTurn turn1 = playGame.getTurn();
        while (!turn1.isFinished()) {
            ActionCard card = ObedientStrategy.getInstance().playTurn(turn1.getTurnInfo());
            turn1.playCard(card);
        }
        assertEquals(PlayerStatus.IN_GAME, playGame.getPlayerInfo(player1).getStatus());
    }

    @Test
    void endGame() throws GameException, TurnException {
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        PlayGame playGame = new Monopoly(ImmutableList.of(player1, player2));
        playGame.start();
        Monopoly monopoly = (Monopoly) playGame;
        monopoly.baseGame.playerData(player1).setStatus(PlayerStatus.OUT_OF_GAME);
        monopoly.baseGame.playerData(player2).setStatus(PlayerStatus.IN_GAME);
        monopoly.baseGame.nextPlayer(); // player2
        monopoly.baseGame.newTurn();    // new turn for player2
        playGame.getTurn().endTurn();
        assertThrows(GameException.class, playGame::getTurn);
        assertFalse(playGame.isGameInProgress());


    }

    @Test
    void doBirthdayParty() throws GameException, TurnException {

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        PlayGame playGame = new Monopoly(ImmutableList.of(player1, player2));
        playGame.start();
        Monopoly monopoly = (Monopoly) playGame;
        // it is player1's birthday. Everyone gives him a present
        monopoly.baseGame.nextPlayer(); // player1
        monopoly.baseGame.newTurn();    // new turn
        monopoly.baseGame.sendCard(player1, BirthdayParty.of());
        monopoly.baseGame.sendCard(player1, EndTurn.of());

        PlayTurn turn1 = monopoly.getTurn();
        ActionCard card = ObedientStrategy.getInstance().playTurn(turn1.getTurnInfo());
        turn1.playCard(card);
        assertTrue(turn1.isFinished());


        PlayTurn turn2 = monopoly.getTurn();
        assertNotSame(turn1, turn2);
        assertNotSame(turn1.getPlayer(), turn2.getPlayer());
        card = ObedientStrategy.getInstance().playTurn(turn2.getTurnInfo());
        turn2.playCard(card);

    }
}