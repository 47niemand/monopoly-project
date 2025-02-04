package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * It is a notification that the player took ownership of the property.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class OwnershipPrivilege extends BaseBuy {

    OwnershipPrivilege(int position) {
        super(ActionType.OBLIGATION, HIGH_PRIORITY, position);
    }

    public static ActionCard create(int position) {
        return new OwnershipPrivilege(position);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.takeProperty(position);
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Unexpected error", e);
        }
        return ImmutableList.of();
    }

}
