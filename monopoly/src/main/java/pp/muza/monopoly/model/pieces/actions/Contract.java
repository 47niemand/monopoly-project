package pp.muza.monopoly.model.pieces.actions;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.Property;

/**
 * A contract for a property.
 * <p>
 * A player who owns the property can sale it to get a profit.
 * This card spawns when the player has no enough money to pay obligations.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Contract extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    /**
     * the id of the land to be traded
     */
    private final int landId;
    /**
     * the property being sent
     */
    private final Property property;

    Contract(int landId, Property property) {
        super("Contract", Action.CONTRACT, Type.CONTRACT, DEFAULT_PRIORITY);
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.doContract(landId);
        } catch (BankException | TurnException e) {
            LOG.info("Player cannot receive money: {}", e.getMessage());
        }
        return ImmutableList.of();
    }
}