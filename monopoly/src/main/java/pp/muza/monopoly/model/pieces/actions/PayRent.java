package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * A player has to pay coins to the property owner on which player is standing.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PayRent extends Payment {

    private static final Logger LOG = LoggerFactory.getLogger(PayRent.class);

    private final int landId;

    PayRent(Player recipient, int value, int landId) {
        super("Pay Rent", ActionType.OBLIGATION, DEFAULT_PRIORITY, recipient, value);
        this.landId = landId;
    }

    @Override
    protected List<ActionCard> onSuccess(Turn turn) {
        // sent rent to the owner
        try {
            turn.sendCard(recipient, new RentRevenue(value, turn.getPlayer(), landId));
        } catch (TurnException e) {
            LOG.error("Error during executing the action: {}", this, e);
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}
