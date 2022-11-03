package pp.muza.monopoly.model.pieces.actions;

import java.util.LinkedHashMap;
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
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    /**
     * the id of the land to be traded.
     */
    protected final int position;
    protected final int price;

    protected Contract(ActionType type, int priority, int position, int price) {
        super(Action.CONTRACT, type, priority);
        this.position = position;
        this.price = price;
    }

    Contract(int position, int price) {
        this(ActionType.CHOICE, HIGHEST_PRIORITY, position, price);
    }

    public static ActionCard create(int position, int price) {
        return new Contract(position, price);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.doContract(position, price);
        } catch (BankException e) {
            LOG.warn("Player cannot receive coins: {}", e.getMessage());
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error during executing the action: " + this, e);
        }
        return ImmutableList.of();
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("position", position, "price", price)
        );
    }
}
