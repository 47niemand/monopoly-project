package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;

import java.util.List;

public class TakeChanceCard extends ActionCard {

    TakeChanceCard() {
        super("Take Chance Card", Action.CHANCE, Type.CHANCE, DEFAULT_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return ImmutableList.of(turn.popChanceCard());
    }
}
