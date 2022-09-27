package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * A player receives coins from this card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Income extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Income.class);

    /**
     * The number of coins that is received.
     */
    protected final int value;

    protected Income(String name, int priority, int value) {
        super(name, Action.INCOME, ActionType.PROFIT, priority);
        this.value = value;
    }

    Income(int value) {
        this("Income", HIGHEST_PRIORITY, value);
    }

    public static ActionCard of(int value) {
        return new Income(value);
    }

    @Override
    protected final List<ActionCard> onExecute(Turn turn) {
        try {
            turn.income(value);
        } catch (BankException e) {
            LOG.warn("Player cannot receive coins: {}", e.getMessage());
        }
        return ImmutableList.of();
    }
}
