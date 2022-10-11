package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;

/**
 * A player receives coins from the card.
 *
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class GoReward extends Income {

    GoReward(int value) {
        super(HIGHEST_PRIORITY, value);
    }

    public static ActionCard of(int value) {
        return new GoReward(value);
    }
}
