package pp.muza.monopoly.model.pieces.actions;


import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * A player has to pay coins to the property owner on which player is standing.
 */
@EqualsAndHashCode(callSuper = true)
public final class PayRent extends Payment {

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
        } catch (pp.muza.monopoly.errors.TurnException e) {
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}
