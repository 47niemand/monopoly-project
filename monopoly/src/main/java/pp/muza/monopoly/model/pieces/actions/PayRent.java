package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * A player has to pay coins to the property owner on which player is standing.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class PayRent extends BasePayment {

    private final int position;

    PayRent(int value, Player recipient, int position) {
        super(ActionType.OBLIGATION, DEFAULT_PRIORITY, value, recipient);
        this.position = position;
    }

    public static ActionCard create(int value, Player recipient, int position) {
        return new PayRent(value, recipient, position);
    }

    @Override
    protected List<ActionCard> onSuccess(Turn turn) {
        // sent rent to the owner
        try {
            turn.sendCard(recipient, new RentRevenue(value, turn.getPlayer(), position));
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error sending rent revenue", e);
        }
        return ImmutableList.of();
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                Map.of("position", position));
    }
}
