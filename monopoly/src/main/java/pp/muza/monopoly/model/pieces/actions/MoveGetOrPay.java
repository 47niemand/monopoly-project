package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * A player can to move on a specific land.
 * If one is available, get it for free, otherwise pay rent to the owner.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class MoveGetOrPay extends MoveTo {

    MoveGetOrPay(int position) {
        super(ActionType.CHOOSE, DEFAULT_PRIORITY, position);
    }

    public static ActionCard create(int position) {
        return new MoveGetOrPay(position);
    }

    @Override
    protected List<ActionCard> onArrival(Turn turn) {
        return ImmutableList.of(new GetOrPay(position));
    }

}
