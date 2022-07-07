package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static pp.muza.monopoly.model.actions.cards.PayRent.createContractsForPlayerPossession;

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
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            turn.payTax(amount);
            result = ImmutableList.of();
        } catch (BankException e) {
            result = new ArrayList<>();
            result.add(this);
            result.addAll(createContractsForPlayerPossession(turn));
        }
        return result;
    }
}
