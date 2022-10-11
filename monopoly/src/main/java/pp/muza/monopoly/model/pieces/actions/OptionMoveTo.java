package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;

/**
 * A player can decide to move on a specific land.
 *
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class OptionMoveTo extends MoveTo {

    OptionMoveTo(int position) {
        super(ActionType.CHOOSE, DEFAULT_PRIORITY, position);
    }

    public static ActionCard of(int position) {
        return new OptionMoveTo(position);
    }

}
