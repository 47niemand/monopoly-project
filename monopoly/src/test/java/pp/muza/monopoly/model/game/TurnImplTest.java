package pp.muza.monopoly.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.pieces.actions.BirthdayParty;
import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.EndTurn;
import pp.muza.monopoly.strategy.ObedientStrategy;

class TurnImplTest {

    @Test
    void getOutOfJail() throws GameException, TurnException {
        // test if the game starts

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));

        // act
        assertFalse(game.isGameInProgress());
        game.start();

        game.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);
        game.baseGame.sendCard(player1, game.baseGame.pickFortuneCard(Chance.GET_OUT_OF_JAIL_FREE));
        PlayTurn turn1 = game.getTurn();
        while (!turn1.isFinished()) {
            ActionCard card = ObedientStrategy.getInstance().playTurn(turn1.getTurnInfo());
            turn1.playCard(card);
        }
        assertEquals(PlayerStatus.IN_GAME, game.getPlayerInfo(player1).getStatus());
    }


    @Test
    void leaveJail() throws GameException, TurnException {
        // test if the game starts

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));

        // act
        assertFalse(game.isGameInProgress());
        game.start();

        game.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);

        PlayTurn turn1 = game.getTurn();
        while (!turn1.isFinished()) {
            ActionCard card = ObedientStrategy.getInstance().playTurn(turn1.getTurnInfo());
            turn1.playCard(card);
        }
        assertEquals(PlayerStatus.IN_GAME, game.getPlayerInfo(player1).getStatus());
    }

    @Test
    void endGame() throws GameException, TurnException {
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.start();
        game.baseGame.playerData(player1).setStatus(PlayerStatus.OUT_OF_GAME);
        game.baseGame.playerData(player2).setStatus(PlayerStatus.IN_GAME);
        game.baseGame.nextPlayer(); // player2
        game.baseGame.newTurn();    // new turn for player2
        game.getTurn().endTurn();
        assertThrows(GameException.class, game::getTurn);
        assertFalse(game.isGameInProgress());


    }

    @Test
    void doBirthdayParty() throws GameException, TurnException {

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.start();
        // it is player1's birthday. Everyone gives him a present
        game.baseGame.nextPlayer(); // player1
        game.baseGame.newTurn();    // new turn
        game.baseGame.sendCard(player1, BirthdayParty.of());
        game.baseGame.sendCard(player1, EndTurn.of());

        PlayTurn turn1 = game.getTurn();
        ActionCard card = ObedientStrategy.getInstance().playTurn(turn1.getTurnInfo());
        turn1.playCard(card);
        assertTrue(turn1.isFinished());


        PlayTurn turn2 = game.getTurn();
        assertNotSame(turn1, turn2);
        assertNotSame(turn1.getPlayer(), turn2.getPlayer());
        card = ObedientStrategy.getInstance().playTurn(turn2.getTurnInfo());
        turn2.playCard(card);

    }
}