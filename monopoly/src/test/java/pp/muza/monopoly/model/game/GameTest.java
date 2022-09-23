package pp.muza.monopoly.model.game;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.bank.BankImpl;
import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.Move;
import pp.muza.monopoly.model.pieces.lands.PropertyColor;
import pp.muza.monopoly.model.game.turn.TurnImpl;
import pp.muza.monopoly.strategy.ObedientStrategy;

class GameTest {

    @Test
    void testCase1() throws GameException, TurnException {
        // player moves by 1 step
        // buys property

        // test setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        List<Player> players = Arrays.asList(player1, player2);

        GameImpl game = new GameImpl(new BankImpl(), BoardLayout.defaultBoard(), ChancePile.defaultPile(), players);
        game.start();

        PlayTurn turn = (PlayTurn) TurnImpl.of(game, player1, 1);
        // test

        game.sendCard(players.get(0), Move.of(1));
        while (!turn.isFinished()) {
            ActionCard card = ObedientStrategy.getInstance().playTurn(turn.getTurnInfo());
            turn.playCard(card);
        }
        Assertions.assertEquals(1, game.playerContext(player1).getPosition());
    }

    @Test
    void gameTurnTestCase1() throws GameException, TurnException {
        // game of 2 players
        // testing scenario:
        // player 1 gets chance card "give this card to a player 3";
        // because player 3 is not in the game, player 1 should get another chance card;
        // the next chance card lets player 1 move to BLUE or ORANGE property;
        // the player should complete the turn by moving to the property and buying it;

        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2")) {
            players.add(new Player(s));
        }
        GameImpl game = new GameImpl(new BankImpl(), BoardLayout.defaultBoard(), ChancePile.defaultPile(), players);
        Player player = players.get(0);
        // test setup
        game.sendCard(player, game.pickFortuneCard(Chance.GIVE_THIS_CARD_TO_A_PLAYER_3));
        game.bringFortuneCardToTop(Chance.ADVANCE_TO_BLUE_OR_ORANGE);
        // test
        PlayTurn turn = game.getTurn();
        while (!turn.isFinished()) {
            turn.playCard(ObedientStrategy.getInstance().playTurn(turn.getTurnInfo()));
        }
        Assertions.assertTrue(game.propertyOwners.entrySet().stream()
                        .anyMatch(x -> x.getValue().equals(player) && (
                                ((Property) game.getBoard().getLand(x.getKey())).getColor() == PropertyColor.BLUE
                                        ||
                                        ((Property) game.getBoard().getLand(x.getKey())).getColor() == PropertyColor.ORANGE)
                        ),
                "Player should own the property");
    }
}