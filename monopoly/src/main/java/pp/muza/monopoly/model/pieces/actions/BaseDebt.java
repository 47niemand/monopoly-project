package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * A base class for paying coins to another player or bank.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class BaseDebt extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDebt.class);

    protected final int value;

    protected BaseDebt(ActionType type, int priority, int value) {
        super(Action.DEBT, type, priority);
        this.value = value;
    }

    /**
     * Return cards if the player succeeds in paying the tax.
     * <p>Override this method in subclasses to return cards if the player succeeds in paying the tax.</p>
     *
     * @param turn the turn to execute the action on.
     * @return cards if the player succeeds in paying the tax.
     */
    protected List<ActionCard> onSuccess(Turn turn) {
        return ImmutableList.of();
    }

    /**
     * Return cards if the player cannot pay the debt.
     *
     * @param turn the turn to execute the action on.
     * @return cards if the player cannot pay the tax.
     */
    protected final List<ActionCard> onFailure(Turn turn) {
        return ImmutableList.<ActionCard>builder().add(this).addAll(CardUtils.sellDebts(turn))
                .build();
    }

    /**
     * Check if the player can pay the debt.
     *
     * @param turn the turn to execute the action on.
     * @throws TurnException if the player cannot pay the tax.
     */
    protected void check(Turn turn) throws TurnException {
        //  check if the player can play the card
    }

    @Override
    protected final List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            check(turn);
            try {
                turn.withdraw(value);
                result = onSuccess(turn);
            } catch (BankException e) {
                LOG.warn("Player {} cannot pay the debt.", turn.getPlayer());
                result = onFailure(turn);
            }
        } catch (TurnException e) {
            LOG.warn("Player cannot play card: {}", e.getMessage());
            result = ImmutableList.of();
        }
        return result;
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("value", value)
        );
    }
}
