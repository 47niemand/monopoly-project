package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * A contract for a property.
 * <p>
 * A player who owns the property can sale it to get a profit.
 * This card spawns when the player has no enough coins to pay obligations.
 * </p>
 *
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    /**
     * the id of the land to be traded.
     */
    protected final int position;

    protected Contract(ActionType type, int priority, int position) {
        super(Action.CONTRACT, type, priority);
        this.position = position;
    }

    Contract(int position) {
        this(ActionType.CHOOSE, HIGHEST_PRIORITY, position);
    }

    public static ActionCard of(int position) {
        return new Contract(position);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.doContract(position);
        } catch (BankException e) {
            LOG.warn("Player cannot receive coins: {}", e.getMessage());
        } catch (TurnException e) {
            LOG.error("Error during executing the action: {}", this, e);
            throw new UnexpectedErrorException(e);
        }
        return ImmutableList.of();
    }
}
