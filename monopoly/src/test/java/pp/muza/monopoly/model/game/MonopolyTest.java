package pp.muza.monopoly.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.consts.Constants;
import pp.muza.monopoly.data.GameInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.pieces.actions.*;
import pp.muza.monopoly.strategy.ObedientStrategy;

class MonopolyTest {

    @Test
    void contractingTest() throws GameException, TurnException {

        Constants.allowAuction = true;
        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly monopoly = new Monopoly(ImmutableList.of(player1, player2));
        // add some assets to players
        monopoly.baseGame.setPropertyOwner(monopoly.baseGame.getGame().findProperty(Asset.MAYFAIR), player1);
        monopoly.baseGame.setPropertyOwner(monopoly.baseGame.getGame().findProperty(Asset.BAKERY), player1);
        monopoly.baseGame.setPropertyOwner(monopoly.baseGame.getGame().findProperty(Asset.THE_ZOO), player2);
        monopoly.baseGame.setPropertyOwner(monopoly.baseGame.getGame().findProperty(Asset.BURGER_JOINT), player2);
        // no money
        monopoly.baseGame.getBank().set(player1, 0);
        monopoly.baseGame.getBank().set(player2, 0);

        // add some obligations to players
        monopoly.baseGame.playerData(player1).addCard(Tax.of(5));
        monopoly.baseGame.playerData(player2).addCard(Tax.of(5));

        // act
        assertFalse(monopoly.isGameInProgress());
        monopoly.start();
        PlayTurn turn = monopoly.getTurn();
        turn.playCard(Tax.of(5));
        System.out.println("Turn: " + turn.getTurnInfo());
        List<Offer> offers = turn.getTurnInfo().getActiveCards().stream().filter(c -> c.getAction() == Action.OFFER).map(c -> (Offer) c).collect(Collectors.toList());
        assertEquals(2, offers.size());
        Offer offer = offers.get(0).openingBid(100);
        turn.playCard(offer);
        assertTrue(turn.isFinished());

        PlayTurn turn2 = monopoly.getTurn();
        System.out.println("Turn: " + turn2.getTurnInfo());
        List<ActionCard> c = turn2.getTurnInfo().getActiveCards();
        System.out.println("Cards: " + c);
        turn2.playCard(Bid.of(4, 10));

    }

    @Test
    void testTurn() throws GameException, TurnException {
        List<Player> players = IntStream.range(0, Constants.MAX_PLAYERS).mapToObj(i -> new Player("@Player" + (i + 1)))
                .collect(Collectors.toList());
        PlayGame game = new Monopoly(players);
        game.start();
        assertTrue(game.isGameInProgress());
        PlayTurn turn = game.getTurn();
        // wrong card
        assertThrows(TurnException.class, () -> turn.playCard(EndTurn.of()));
        // play turn for player 1
        while (!turn.isFinished()) {
            turn.playCard(ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn.getTurnInfo()));
        }
        // play turn for player 2
        PlayTurn turn2 = game.getTurn();
        // wrong turn
        assertThrows(TurnException.class, () -> turn.playCard(Takeover.of(1)));
        turn2.endTurn();
    }

    @Test
    void gameLoop() throws GameException, TurnException {
        List<Player> players = IntStream.range(0, Constants.MAX_PLAYERS).mapToObj(i -> new Player("@Player" + (i + 1)))
                .collect(Collectors.toList());
        PlayGame game = new Monopoly(players);
        game.start();
        while (game.isGameInProgress()) {
            PlayTurn turn = game.getTurn();
            ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn.getTurnInfo());
            if (card != null) {
                turn.playCard(card);
            } else {
                turn.endTurn();
            }
            if (game.getTurnNumber() > Constants.DEFAULT_MAX_TURNS) {
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
        turn.playCard(ObedientStrategy.getInstance().playTurn(playGame.getBoard(), playGame.getPlayers(), turn.getTurnInfo()));
        turn.playCard(ObedientStrategy.getInstance().playTurn(playGame.getBoard(), playGame.getPlayers(), turn.getTurnInfo()));

        // check the game state
        GameInfo gameInfo = playGame.getGameInfo();
        String gameInfoString = gameInfo.toString();

        // restore new game with the same state as the old one
        Monopoly playGame2 = new Monopoly(gameInfo);

        // check if the game state is the same
        assertEquals(gameInfoString, playGame2.getGameInfo().toString());

        // make some turns on both instances of the game
        PlayTurn turn2 = playGame2.getTurn();
        turn2.playCard(ObedientStrategy.getInstance().playTurn(playGame2.getBoard(), playGame2.getPlayers(), turn2.getTurnInfo()));
        turn.playCard(ObedientStrategy.getInstance().playTurn(playGame.getBoard(), playGame.getPlayers(), turn.getTurnInfo()));

        // check if the game state is the same
        assertEquals(playGame.getGameInfo().toString(), playGame2.getGameInfo().toString());
    }

    @Test
    void getOutOfJail() throws GameException, TurnException {

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.baseGame.getBank().set(player1, 0);
        game.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);
        game.baseGame.sendCardTest(player1, game.baseGame.pickFortuneCard(Chance.GET_OUT_OF_JAIL_FREE));

        // act
        assertFalse(game.isGameInProgress());
        game.start();
        PlayTurn turn1 = game.getTurn();
        turn1.playCard(FortuneCard.of(Chance.GET_OUT_OF_JAIL_FREE));

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
        game.baseGame.playerData(player1).setStatus(PlayerStatus.IN_JAIL);
        // set the player's money to exactly JAIL_FINE
        game.baseGame.getBank().set(player1, game.baseGame.getGame().getJailFine());

        // act
        assertFalse(game.isGameInProgress());
        game.start();
        assertEquals(PlayerStatus.IN_JAIL, game.getPlayerStatus(player1));
        PlayTurn turn1 = game.getTurn();
        ActionCard card = ObedientStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn1.getTurnInfo());
        turn1.playCard(card);
        List<ActionCard> activeCards = game.baseGame.playerData(player1).getActiveCards();

        // check if the player has the Jail Fine card
        assertEquals(1, activeCards.size());
        assertEquals(Action.DEBT, activeCards.get(0).getAction());
        assertTrue(activeCards.get(0) instanceof JailFine);

        // pay fine
        turn1.playCard(activeCards.get(0));
        // check if the player is out of jail
        assertEquals(PlayerStatus.IN_GAME, game.getPlayerInfo(player1).getStatus());
        // check if right ammo was taken from the player
        assertEquals(0, game.getBalance(player1));
    }

    @Test
    void endGame() throws GameException, TurnException {

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.baseGame.playerData(player1).setStatus(PlayerStatus.OUT_OF_GAME);
        game.baseGame.playerData(player2).setStatus(PlayerStatus.IN_GAME);

        // act
        game.start();
        game.baseGame.getTurn();    // new turn for player2
        game.getTurn().endTurn();

        // check
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
        game.start();
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
        // player1 has a birthday. Each player should pay him $1 (Constants.BIRTHDAY_GIFT_AMOUNT)

        // setup
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Player player3 = new Player("player3");
        Player player4 = new Player("player4");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2, player3, player4));
        game.start();


        game.baseGame.getBank().set(player1, 0); // player1 has no money
        game.baseGame.sendCardTest(player1, BirthdayParty.of());
        game.baseGame.sendCardTest(player1, EndTurn.of());

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

        assertEquals(Constants.BIRTHDAY_GIFT_AMOUNT * 3, game.baseGame.getBank().getBalance(player1));
    }

    @Test
    void moveTo() throws GameException {

        Player player1 = new Player("player1");
        Player player2 = new Player("player2");
        Monopoly game = new Monopoly(ImmutableList.of(player1, player2));
        game.baseGame.playerData(player1).setPosition(1);
        // board should have at least 23 lands
        assert game.getBoard().size() >= 23;

        // stay at the same position
        List<Land> l = game.baseGame.getGame().moveTo(player1, 1);
        assertEquals(1, game.baseGame.playerData(player1).getPosition());
        assertEquals(0, l.size());

        // move to the next position
        l = game.baseGame.getGame().moveTo(player1, 23);
        assertEquals(23, game.baseGame.playerData(player1).getPosition());
        assertEquals(22, l.size());
    }

}