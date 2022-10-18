package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;

/**
 * A player can decide to move on a specific land.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@Deprecated
public final class OptionMoveTo extends MoveTo {

    OptionMoveTo(int position) {
        super(ActionType.CHOOSE, DEFAULT_PRIORITY, position);
    }

    public static ActionCard create(int position) {
        return new OptionMoveTo(position);
    }

}
