package pp.muza.monopoly.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.bank.BankImpl;
import pp.muza.monopoly.model.pieces.actions.EndTurn;
import pp.muza.monopoly.model.pieces.actions.MoveTo;
import pp.muza.monopoly.model.turn.TurnImpl;
import pp.muza.monopoly.strategy.DefaultStrategy;
import pp.muza.monopoly.strategy.ObedientStrategy;
import pp.muza.monopoly.utils.ChancePile;
import pp.muza.monopoly.utils.MonopolyBoard;

class GameTest {

    @Test
    void gameLoop1() {
        // test game loop with four players
        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2", "@Player3", "@Player4")) {
            players.add(new Player(s));
        }
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), players, ChancePile.defaultPile(), ImmutableList.of(DefaultStrategy.STRATEGY), new BankImpl());
        int a = game.fortuneCards.size();
        String a1 = game.fortuneCards.stream().sorted(Comparator.comparing(Fortune::getChance)).collect(Collectors.toList()).toString();
        game.gameLoop();
        game.endGame();
        int b = game.fortuneCards.size();
        String b1 = game.fortuneCards.stream().sorted(Comparator.comparing(Fortune::getChance)).collect(Collectors.toList()).toString();
        Assertions.assertEquals(a, b, "FortuneCard cards should be the same size");
        Assertions.assertEquals(a1, b1, "FortuneCard cards should be the same");
    }

    @Test
    void gameTurnTestCase1() {
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
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), players, ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());
        Player player = players.get(0);
        // test setup
        game.sendCard(player, game.pickFortuneCard(Fortune.Chance.GIVE_THIS_CARD_TO_A_PLAYER_3));
        game.bringFortuneCardToTop(Fortune.Chance.ADVANCE_TO_BLUE_OR_ORANGE);
        // test
        Turn turn = new TurnImpl(game, player);
        game.playTurn(turn);
        Assertions.assertTrue(game.propertyOwners.entrySet().stream()
                        .anyMatch(x -> x.getValue().equals(player) &&
                                (((Property) game.board.getLand(x.getKey())).getColor() == Property.Color.BLUE
                                        || ((Property) x).getColor() == Property.Color.ORANGE)),
                "Player should own the property");
    }

    @Test
    void gameTurnTestCase2() {
        // game of three players
        // testing scenario:
        // player 1 gets chance card BIRTHDAY
        // other players should gift to player1.
        // player2 has no money and should be bankrupted


        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2", "@Player3")) {
            players.add(new Player(s));
        }
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), players, ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);
        // test setup
        game.sendCard(player1, game.pickFortuneCard(Fortune.Chance.BIRTHDAY));
        game.bank.set(player1, BigDecimal.valueOf(10));
        game.bank.set(player2, BigDecimal.valueOf(0)); // player2 has no money and should be bankrupted
        game.bank.set(player3, BigDecimal.valueOf(10));
        // test
        Turn turn = new TurnImpl(game, player1);
        game.playTurn(turn);
        Assertions.assertEquals(PlayerStatus.OUT_OF_GAME, game.playerData.get(player2).getStatus(), "Player should be bankrupt");
        Assertions.assertEquals(BigDecimal.valueOf(11), game.bank.getBalance(player1), "Player should have 11 money");
        Assertions.assertEquals(BigDecimal.valueOf(9), game.bank.getBalance(player3), "Player should have 9 money");
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
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), players, ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        // test setup
        game.sendCard(player1, game.pickFortuneCard(Fortune.Chance.GIVE_THIS_CARD_TO_A_PLAYER_2));
        game.bringFortuneCardToTop(Fortune.Chance.GET_OUT_OF_JAIL_FREE);
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
        Assertions.assertEquals(PlayerStatus.IN_GAME, game.playerData.get(player1).getStatus());
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
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), players, ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());
        Player player = players.get(0);

        // test setup
        game.sendCard(player, game.pickFortuneCard(Fortune.Chance.MOVE_FORWARD_ONE_SPACE));
        game.sendCard(player, EndTurn.of());
        // test
        Turn turn = new TurnImpl(game, player);
        game.playTurn(turn);
        System.out.println(game.getPlayerInfo(player));
    }

    @Test
    void gameTurnTestCase5() {
        // testing scenario:
        // player 1 owns all pieces of the same color;
        // player 2 should pay double rent.

        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2")) {
            players.add(new Player(s));
        }
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), players, ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());
        List<Integer> landsOfSameColor = game.findLandsByColor(Property.Color.BLUE);
        int destination = landsOfSameColor.get(0);
        landsOfSameColor.forEach(land -> game.setPropertyOwner(land, player1));
        // test setup, player1 owns all pieces of the same color
        // player2 have 10 + rent of the property
        game.bank.set(player2, BigDecimal.valueOf(10).add(((Property) game.board.getLand(destination)).getPrice().multiply(BigDecimal.valueOf(2))));
        game.sendCard(player2, MoveTo.of(destination));
        // test
        Turn turn = new TurnImpl(game, player2);
        game.playTurn(turn);
        Assertions.assertEquals(BigDecimal.valueOf(10), game.bank.getBalance(player2), "Player should have 10 money");
    }

    @Test
    void getGameInfo() {
        // test saving and restoring game state

        List<Player> players = new ArrayList<>();
        for (String s : Arrays.asList("@Player1", "@Player2")) {
            players.add(new Player(s));
        }
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), players, ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());
        game.maxTurns = 10;
        // play 10 turns
        game.gameLoop();
        // save game state
        GameInfo gameInfo = game.getGameInfo();

        // create new game with same players and same game state
        GameImpl game2 = new GameImpl(gameInfo, ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());
        GameInfo gameInfo2 = game2.getGameInfo();

        // test if game state is the same
        Assertions.assertEquals(gameInfo, gameInfo2);

        // continue playing game
        game2.maxTurns = 100;
        game2.gameLoop();
    }
}