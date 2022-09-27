package pp.muza.monopoly.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.consts.Meta;
import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.PlayGame;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.strategy.ObedientStrategy;

class MonopolyTest {

    @Test
    void gameLoop() throws GameException, TurnException {
        List<Player> players = IntStream.range(0, Meta.MAX_PLAYERS).mapToObj(i -> new Player("@Player" + (i + 1)))
                .collect(Collectors.toList());
        PlayGame game = new Monopoly(players);
        game.start();
        while (game.isGameInProgress()) {
            PlayTurn turn = game.getTurn();
            ActionCard card = ObedientStrategy.getInstance().playTurn(turn.getTurnInfo());
            if (card != null) {
                turn.playCard(card);
            } else {
                turn.endTurn();
            }
            if (game.getTurnNumber() > Meta.DEFAULT_MAX_TURNS) {
                break;
            }
        }
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
    void getGameInfo() throws GameException, TurnException {
        // test case: use getGameInfo to restore the game's state.

        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly playGame = new Monopoly(ImmutableList.of(player1, player2));

        // start the game and make some turns
        playGame.start();
        PlayTurn turn = playGame.getTurn();
        turn.playCard(ObedientStrategy.getInstance().playTurn(turn.getTurnInfo()));
        turn.playCard(ObedientStrategy.getInstance().playTurn(turn.getTurnInfo()));

        // check the game state
        GameInfo gameInfo = playGame.getGameInfo();
        String gameInfoString = gameInfo.toString();

        // restore new game with the same state as the old one
        Monopoly playGame2 = new Monopoly(gameInfo);

        // check if the game state is the same
        assertEquals(gameInfoString, playGame2.getGameInfo().toString());

        // make some turns on both instances of the game
        PlayTurn turn2 = playGame2.getTurn();
        turn2.playCard(ObedientStrategy.getInstance().playTurn(turn2.getTurnInfo()));
        turn.playCard(ObedientStrategy.getInstance().playTurn(turn.getTurnInfo()));

        // check if the game state is the same
        assertEquals(playGame.getGameInfo().toString(), playGame2.getGameInfo().toString());

    }
}