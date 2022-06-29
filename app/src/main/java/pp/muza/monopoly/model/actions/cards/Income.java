package pp.muza.monopoly.model.actions.cards;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.turn.Turn;

/**
 * The player receives money from this card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Income extends ActionCard {
    private final BigDecimal amount;

    Income(BigDecimal amount) {
        super("Income", Action.INCOME, Type.OBLIGATION, HIGH_PRIORITY);
        this.amount = amount;
    }

    public static ActionCard of(BigDecimal amount) {
        return new Income(amount);
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        try {
            turn.addMoney(amount);
        } catch (BankException e) {
            throw new ActionCardException(e, this);
        }
    }
}
