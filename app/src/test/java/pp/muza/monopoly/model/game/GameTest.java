package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import pp.muza.monopoly.model.actions.ChanceCard;
import pp.muza.monopoly.model.actions.cards.EndTurn;
import pp.muza.monopoly.model.game.strategy.ObedientStrategy;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GameTest {

    @Test
    void gameLoop1() {
        // test game loop with four players
        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2", "@Player3", "@Player4")) {
            players.add(new Player(s));
        }

        Game game = new Game(players);
        game.gameLoop();
    }


    @Test
    void gameTurnTestCase1() {
        // game of 2 players
        // testing scenario:
        // player 1 gets chance card "give this card to a player 3"
        // because player 3 is not in the game, player 1 should get another chance card
        // the next chance card lets player 1 move to BLUE or ORANGE property
        // the player should complete the turn by moving to the property and buying it

        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2")) {
            players.add(new Player(s));
        }
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.strategy));
        Player player = players.get(0);
        // test setup
        game.sendCardToPlayer(player, game.removeChanceCard(ChanceCard.GIVE_THIS_CARD_TO_A_PLAYER_3));
        game.bringChanceCardToTop(ChanceCard.ADVANCE_TO_BLUE_OR_ORANGE);
        // test
        Turn turn = new TurnImpl(game, player);
        game.playTurn(turn);
    }

    @Test
    void gameTurnTestCase2() throws BankException {
        // game of three players
        // testing scenario:
        // player 1 gets chance card BIRTHDAY
        // other players should gift to player1.
        // player2 has no money and should be bankrupted


        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2", "@Player3")) {
            players.add(new Player(s));
        }
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.strategy));
        Player player = players.get(0);
        // test setup
        game.sendCardToPlayer(player, game.removeChanceCard(ChanceCard.BIRTHDAY));
        game.withdraw(players.get(1), BigDecimal.valueOf(18));
        // test
        Turn turn = new TurnImpl(game, player);
        game.playTurn(turn);
    }

    @Test
    void gameTurnTestCase3() {
        // game of three players
        // testing scenario:
        // player 1 gets chance card GIVE_THIS_CARD_TO_A_PLAYER_2, card should pass the card to player 2.
        // player 2 uses gift card to choose any property to his/her choice
        // player 1 got the GET_OUT_OF_JAIL_FREE card, should be able to get out of jail free.

        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2")) {
            players.add(new Player(s));
        }
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.strategy));
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        // test setup
        game.sendCardToPlayer(player1, game.removeChanceCard(ChanceCard.GIVE_THIS_CARD_TO_A_PLAYER_2));
        game.bringChanceCardToTop(ChanceCard.GET_OUT_OF_JAIL_FREE);
        // test
        Turn turn = new TurnImpl(game, player1);
        game.sendCardToPlayer(player1, EndTurn.of());
        game.playTurn(turn);
        System.out.println(game.getPlayerInfo1(player1));
        turn = new TurnImpl(game, player2);
        game.playTurn(turn);
        game.setPlayerStatus(player1, PlayerStatus.IN_JAIL);
        turn = new TurnImpl(game, player1);
        game.playTurn(turn);
        System.out.println(game.getPlayerInfo1(player2));
    }
}