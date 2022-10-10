package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;

/**
 * A player can decide to move on a specific distance.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class OptionMove extends Move {

    OptionMove(int distance) {
        super("Choice to Move", ActionType.CHOOSE, DEFAULT_PRIORITY, distance);
    }

    public static ActionCard of(int distance) {
        return new OptionMove(distance);
    }
}
