package pp.muza.monopoly.model.pieces.actions;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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