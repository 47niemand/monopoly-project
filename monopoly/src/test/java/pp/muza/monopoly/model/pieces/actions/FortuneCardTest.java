package pp.muza.monopoly.model.pieces.actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PropertyColor;
import pp.muza.monopoly.model.Turn;

class FortuneCardTest {

    @Test
    void onExecute() {

        List<Fortune> fortunes = Arrays
                .stream(Chance.values())
                .map(FortuneCard::of)
                .collect(Collectors.toList());

        Player player1 = new Player("test1");
        Turn turn = mock(Turn.class);
        when(turn.getPlayer()).thenReturn(player1);
        when(turn.foundLandsByColor(ArgumentMatchers.any(PropertyColor.class))).thenReturn(ImmutableList.of(0));
        when(turn.popFortuneCard()).thenReturn(fortunes.get(0));

        for (Fortune fortune : fortunes) {
            List<ActionCard> a = ((BaseActionCard) fortune).onExecute(turn);
            assertFalse(a.isEmpty(), "Result should not be empty: " + fortune);
        }
    }
}