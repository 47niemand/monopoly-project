package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * A Player can use this card to take the top fortune card from the deck.
 *
 * @author dmytromuza
 */
@Getter

@EqualsAndHashCode(callSuper = true)
public final class TakeFortuneCard extends BaseActionCard {

    TakeFortuneCard() {
        super(Action.GIFT, ActionType.CHOOSE, DEFAULT_PRIORITY);
    }

    public static ActionCard create() {
        return new TakeFortuneCard();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return ImmutableList.of(turn.popFortuneCard());
    }
}
