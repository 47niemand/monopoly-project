package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;

public final class TakeFortuneCard extends BaseActionCard {

    TakeFortuneCard() {
        super("Take Fortune Card", Action.GIFT, Type.CHOOSE, DEFAULT_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return ImmutableList.of(turn.popFortuneCard());
    }
}
