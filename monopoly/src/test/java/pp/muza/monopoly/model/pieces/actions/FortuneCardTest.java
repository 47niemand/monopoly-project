package pp.muza.monopoly.model.pieces.actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.bank.BankImpl;
import pp.muza.monopoly.model.game.GameImpl;
import pp.muza.monopoly.model.turn.TurnImpl;
import pp.muza.monopoly.strategy.ObedientStrategy;
import pp.muza.monopoly.utils.ChancePile;
import pp.muza.monopoly.utils.MonopolyBoard;

class FortuneCardTest {

    @Test
    void onExecute() {

        Player player = new Player("@Player1");
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), ImmutableList.of(player), ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.getInstance()), new BankImpl());

        List<Fortune> fortunes = Arrays
                .stream(Chance.values())
                .map(FortuneCard::of)
                .collect(Collectors.toList());

        Turn a = new TurnImpl(game, player);

        for (Fortune fortune : fortunes) {
            List<ActionCard> result = ((BaseActionCard) fortune).onExecute(a);
            Assertions.assertNotNull(result, String.format("FortuneCard (%s) should be executed", fortune));
        }
    }

    @Test
    void fortuneMoveForwardOneSpace() {
        // test setup
        Player player = new Player("@Player1");
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), ImmutableList.of(player), ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.getInstance()), new BankImpl());
        Turn a = new TurnImpl(game, player);
        Fortune fortune = FortuneCard.of(Chance.MOVE_FORWARD_ONE_SPACE);

        // test
        List<ActionCard> result = ((BaseActionCard) fortune).onExecute(a);
        Assertions.assertEquals(2, result.size(), "Two options should be returned");
        Assertions.assertTrue(result.contains(new OptionMove(1)), "OptionMove(1) should be returned");
        Assertions.assertTrue(result.contains(new TakeFortuneCard()), "TakeFortuneCard should be returned");
    }
}