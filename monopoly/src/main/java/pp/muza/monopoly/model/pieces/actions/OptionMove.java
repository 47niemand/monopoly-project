package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;

/**
 * A player can decide to move on a specific distance.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class OptionMove extends Move {

    OptionMove(int distance) {
        super(ActionType.CHOOSE, DEFAULT_PRIORITY, distance);
    }

    public static ActionCard create(int distance) {
        return new OptionMove(distance);
    }
}
