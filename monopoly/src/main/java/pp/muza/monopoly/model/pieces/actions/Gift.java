package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;

/**
 * A player has to pay coins to the property owner on which player is standing.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Gift extends Payment {

    Gift(Player recipient, int value) {
        super("Gift", ActionType.OBLIGATION, HIGH_PRIORITY, recipient, value);
    }

    public static ActionCard of(int value, Player recipient) {
        return new Gift(recipient, value);
    }

}
