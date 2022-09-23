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
 * A contract for a property.
 * <p>
 * A player who owns the property can sale it to get a profit.
 * This card spawns when the player has no enough coins to pay obligations.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Contract extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    /**
     * the id of the land to be traded.
     */
    private final int landId;

    Contract(int landId) {
        super("Contract", Action.CONTRACT, ActionType.CHOOSE, HIGHEST_PRIORITY);
        this.landId = landId;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.doContract(landId);
        } catch (BankException e) {
            LOG.warn("Player cannot receive coins: {}", e.getMessage());
        } catch (pp.muza.monopoly.errors.TurnException e) {
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}
