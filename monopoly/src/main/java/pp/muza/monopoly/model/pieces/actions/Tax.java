package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;

/**
 * A base class for all actions that are related to payment to the bank.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Tax extends BaseDebt {

    protected Tax(ActionType type, int priority, int value) {
        super(type, priority, value);
    }

    Tax(int value) {
        this(ActionType.OBLIGATION, DEFAULT_PRIORITY, value);
    }

    public static ActionCard create(int value) {
        return new Tax(value);
    }
}
