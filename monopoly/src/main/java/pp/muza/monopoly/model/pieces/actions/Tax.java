package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;

/**
 * A base class for all actions that are related to payment to the bank.
 *
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Tax extends Debt {

    protected Tax(ActionType type, int priority, int value) {
        super(type, priority, value);
    }

    Tax(int value) {
        this(ActionType.OBLIGATION, DEFAULT_PRIORITY, value);
    }

    public static ActionCard of(int value) {
        return new Tax(value);
    }
}
