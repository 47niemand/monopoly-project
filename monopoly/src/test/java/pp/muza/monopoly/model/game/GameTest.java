package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pp.muza.monopoly.model.actions.Chance;
import pp.muza.monopoly.model.actions.ChanceCard;
import pp.muza.monopoly.model.actions.EndTurn;
import pp.muza.monopoly.strategy.DefaultStrategy;
import pp.muza.monopoly.strategy.ObedientStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class GameTest {

    @Test
    void gameLoop1() {
        for (int i = 0; i < 100; i++) {
            // test game loop with four players
            List<Player> players = new ArrayList<>();
            for (String s : Arrays.asList("@Player1", "@Player2", "@Player3", "@Player4")) {
                players.add(new Player(s));
            }
            Game game = new Game(players, DefaultStrategy.STRATEGY);
            int a = game.getChanceCards().size();
            String a1 = game.getChanceCards().stream().sorted(Comparator.comparing(Chance::getCard)).collect(Collectors.toList()).toString();
            game.gameLoop();
            game.endGame();
            int b = game.getChanceCards().size();
            String b1 = game.getChanceCards().stream().sorted(Comparator.comparing(Chance::getCard)).collect(Collectors.toList()).toString();
            Assertions.assertEquals(a, b, "Chance cards should be the same size");
            Assertions.assertEquals(a1, b1, "Chance cards should be the same");
        }
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
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.STRATEGY));
        Player player = players.get(0);
        // test setup
        game.sendCard(player, game.removeChanceCard(ChanceCard.GIVE_THIS_CARD_TO_A_PLAYER_3));
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
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.STRATEGY));
        Player player = players.get(0);
        // test setup
        game.sendCard(player, game.removeChanceCard(ChanceCard.BIRTHDAY));
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
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.STRATEGY));
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        // test setup
        game.sendCard(player1, game.removeChanceCard(ChanceCard.GIVE_THIS_CARD_TO_A_PLAYER_2));
        game.bringChanceCardToTop(ChanceCard.GET_OUT_OF_JAIL_FREE);
        // test
        Turn turn = new TurnImpl(game, player1);
        game.sendCard(player1, EndTurn.of());
        game.playTurn(turn);
        System.out.println(game.getPlayerInfo(player1));
        turn = new TurnImpl(game, player2);
        game.playTurn(turn);
        game.setPlayerStatus(player1, PlayerStatus.IN_JAIL);
        turn = new TurnImpl(game, player1);
        game.playTurn(turn);
        System.out.println(game.getPlayerInfo(player2));
    }

    @Test
    void gameTurnTestCase4() {
        // testing scenario:
        // player 1 gets chance card MOVE_FORWARD_ONE_SPACE,
        // player 1 should decide to move forward one space or take a chance card.

        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2")) {
            players.add(new Player(s));
        }
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.STRATEGY));
        Player player = players.get(0);

        // test setup
        game.sendCard(player, game.removeChanceCard(ChanceCard.MOVE_FORWARD_ONE_SPACE));
        game.sendCard(player, EndTurn.of());
        // test
        Turn turn = new TurnImpl(game, player);
        game.playTurn(turn);
        System.out.println(game.getPlayerInfo(player));
    }

    @Test
    void getGameInfo() {
        // test saving and restoring game state

        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2")) {
            players.add(new Player(s));
        }
        Game game = new Game(players, ImmutableList.of(ObedientStrategy.STRATEGY));
        game.maxTurns = 10;
        // play 10 turns
        game.gameLoop();
        // save game state
        GameInfo gameInfo = game.getGameInfo();

        // create new game with same players and same game state
        Game game2 = new Game(gameInfo, ImmutableList.of(ObedientStrategy.STRATEGY));
        GameInfo gameInfo2 = game2.getGameInfo();

        // test if game state is the same
        Assertions.assertEquals(gameInfo, gameInfo2);

        // continue playing game
        game2.maxTurns = 100;
        game2.gameLoop();
    }
}