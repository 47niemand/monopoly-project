package pp.muza.monopoly.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.consts.Meta;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Asset;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.BirthdayParty;
import pp.muza.monopoly.model.pieces.actions.Buy;
import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.EndTurn;
import pp.muza.monopoly.model.pieces.actions.JailFine;
import pp.muza.monopoly.model.pieces.actions.NewTurn;
import pp.muza.monopoly.model.pieces.actions.RentRevenue;
import pp.muza.monopoly.strategy.ObedientStrategy;

class BaseTurnImplTest {

    @Test
    void getOutOfJail() throws GameException, TurnException {

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));

        // act
        assertFalse(game.isGameInProgress());
        game.start();

        game.baseGame.getBank().set(player1, 0);
        game.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);
        game.baseGame.sendCard(player1, game.baseGame.pickFortuneCard(Chance.GET_OUT_OF_JAIL_FREE));
        PlayTurn turn1 = game.getTurn();
        ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
        turn1.playCard(card);

        // check if there is new_turn card in the player's hand
        assertEquals(1, game.baseGame.playerData(player1).getCards().size());
        assertEquals(Action.NEW_TURN, game.baseGame.playerData(player1).getCards().get(0).getAction());
        // check if the player is out of jail
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
        game.baseGame.getBank().set(player1, game.baseGame.getGame().getJailFine());

        PlayTurn turn1 = game.getTurn();
        ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
        turn1.playCard(card);

        List<ActionCard> activeCards = game.baseGame.playerData(player1).getActiveCards();

        assertEquals(1, activeCards.size());
        assertEquals(Action.DEBT, activeCards.get(0).getAction());
        assertTrue(activeCards.get(0) instanceof JailFine);

        turn1.playCard(activeCards.get(0));

        assertEquals(PlayerStatus.IN_GAME, game.getPlayerInfo(player1).getStatus());
        assertEquals(0, game.getBalance(player1));
    }

    @Test
    void leaveJailByCard() throws GameException, TurnException {

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));

        // act
        game.start();

        game.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);
        game.baseGame.playerData(player1).addCard(game.baseGame.pickFortuneCard(Chance.GET_OUT_OF_JAIL_FREE));

        PlayTurn turn1 = game.getTurn();
        while (!turn1.isFinished()) {
            ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
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
        game.baseGame.getTurn();    // new turn for player2
        game.getTurn().endTurn();
        assertThrows(GameException.class, game::getTurn);
        assertFalse(game.isGameInProgress());
    }

    @Test
    void buy() throws GameException, TurnException {
        // setup:
        // create a game with 2 players.
        // player 1 has to buy a property, but he has no money.
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.start();
        game.baseGame.getBank().set(player1, 0);
        int position = game.baseGame.getGame().findProperty(Asset.MAYFAIR);
        game.baseGame.playerData(player1).setPosition(position);
        ActionCard buy = Buy.of(position);
        assert buy.getType().isMandatory(); // buy is mandatory
        game.baseGame.playerData(player1).addCard(Buy.of(position));

        game.baseGame.playerData(player1).addCard(EndTurn.of());
        // player 1 owns some properties
        game.baseGame.setPropertyOwner(game.baseGame.getGame().findProperty(Asset.THE_ZOO), player1);
        assert Asset.THE_ZOO.getPrice() < Asset.MAYFAIR.getPrice(); // the zoo is cheaper than mayfair

        // act
        game.baseGame.getTurn();    // player1 new turn

        PlayTurn turn1 = game.getTurn();
        while (!turn1.isFinished()) {
            ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
            if (card != null) {
                turn1.playCard(card);
            } else {
                turn1.endTurn();
            }
        }

        // assert
        assertEquals(PlayerStatus.OUT_OF_GAME, game.getPlayerInfo(player1).getStatus());
    }

    @Test
    void newTurnWithCardsOnHand() throws GameException, TurnException {

        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.start();
        int land = game.baseGame.getGame().findProperty(Asset.THE_ZOO);
        game.baseGame.getBank().set(player1, 0);
        game.baseGame.playerData(player1).addCard(RentRevenue.of(Asset.THE_ZOO.getPrice(), player2, land));

        // act
        PlayTurn turn1 = game.getTurn();

        ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
        turn1.playCard(card);

        card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
        assertEquals(NewTurn.class, card.getClass());

        // assert
        assertEquals(Asset.THE_ZOO.getPrice(), game.baseGame.getBank().getBalance(player1));
    }

    @Test
    void doBirthdayParty() throws GameException, TurnException {

        // Birthday party scenario:
        // player1 has a birthday. Each player should pay him $1 (Meta.BIRTHDAY_GIFT_AMOUNT)

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Player player3 = new Player("player3");
        Player player4 = new Player("player4");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2, player3, player4));
        game.start();


        game.baseGame.getBank().set(player1, 0); // player1 has no money
        game.baseGame.sendCard(player1, BirthdayParty.of());
        game.baseGame.sendCard(player1, EndTurn.of());

        {
            PlayTurn turn1 = game.getTurn();
            assert game.baseGame.currentPlayerIndex == 0;
            ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
            assertEquals(BirthdayParty.class, card.getClass());
            turn1.playCard(card);
            assertTrue(turn1.isFinished());
            assertEquals(player1, turn1.getPlayer());
        }

        {
            PlayTurn turn2 = game.getTurn();
            assertEquals(player2, turn2.getPlayer());
            ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn2.getTurnInfo());
            turn2.playCard(card);
            turn2.endTurn();
            assertTrue(turn2.isFinished());
            assertEquals(PlayerStatus.IN_GAME, game.getPlayerStatus(player2));
        }

        {
            PlayTurn turn3 = game.getTurn();
            assertEquals(player3, turn3.getPlayer());
            ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn3.getTurnInfo());
            turn3.playCard(card);
            turn3.endTurn();
            assertTrue(turn3.isFinished());
            assertEquals(PlayerStatus.IN_GAME, game.getPlayerStatus(player3));
        }

        {
            PlayTurn turn4 = game.getTurn();
            assertEquals(player4, turn4.getPlayer());
            ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn4.getTurnInfo());
            turn4.playCard(card);
            turn4.endTurn();
            assertTrue(turn4.isFinished());
            assertEquals(PlayerStatus.IN_GAME, game.getPlayerStatus(player4));
        }

        {
            PlayTurn turn5 = game.getTurn();
            assertEquals(player1, turn5.getPlayer());
            while (!turn5.isFinished()) {
                ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn5.getTurnInfo());
                turn5.playCard(card);
            }
            assertTrue(turn5.isFinished());
        }

        assertEquals(Meta.BIRTHDAY_GIFT_AMOUNT * 3, game.baseGame.getBank().getBalance(player1));
    }
}