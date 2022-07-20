package pp.muza.monopoly.model.pieces.actions;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;

/**
 * A player receives money from this card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class StartBonus extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(StartBonus.class);

    private final BigDecimal amount;

    StartBonus(BigDecimal amount) {
        super("Start Bonus", Action.INCOME, Type.OBLIGATION, HIGHEST_PRIORITY);
        this.amount = amount;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.income(amount);
        } catch (BankException e) {
            LOG.info("Player cannot receive money: {}", e.getMessage());
        }
        return ImmutableList.of();
    }
}
