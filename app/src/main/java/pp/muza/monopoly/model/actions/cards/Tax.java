package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.turn.Turn;

import java.math.BigDecimal;

/**
 * The player has to pay money to the bank.
 * <p>
 * if the player is in jail, successfully pay the bill will allow to end the turn.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Tax extends ActionCard {
    private final BigDecimal amount;

    Tax(BigDecimal amount) {
        super("Tax", Action.TAX, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.amount = amount;
    }

    public static Tax of(BigDecimal amount) {
        return new Tax(amount);
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        try {
            turn.payTax(amount);
            turn.leaveJail();
        } catch (BankException e) {
            //TODO: cover by test
            boolean contractCreated = !ActionUtils.createContractsForPlayersPossession(turn);
            throw new ActionCardException(e, this, contractCreated);
        }
    }
}
