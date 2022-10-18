package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;

/**
 * A player sends a gift to another player.
 *
 * @author dmytromuza
 */
@Getter

@EqualsAndHashCode(callSuper = true)
public final class Gift extends Payment {

    Gift(int value, Player recipient) {
        super(ActionType.OBLIGATION, HIGH_PRIORITY, value, recipient);
    }

    public static ActionCard create(int value, Player recipient) {
        return new Gift(value, recipient);
    }

}
