package pp.muza.monopoly.model.pieces.actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

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
        GameImpl game = new GameImpl(MonopolyBoard.defaultBoard(), ImmutableList.of(player), ChancePile.defaultPile(), ImmutableList.of(ObedientStrategy.STRATEGY), new BankImpl());

        List<Fortune> fortunes = Arrays
                .stream(Fortune.Chance.values())
                .map(FortuneCard::of)
                .collect(Collectors.toList());

        Turn a = new TurnImpl(game, player);

        for (Fortune fortune : fortunes) {
            ((BaseActionCard) fortune).onExecute(a);
            Assertions.assertTrue(true, "FortuneCard should be executed");
        }
    }
}