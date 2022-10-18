package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnError;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Turn;

/**
 * If the player is in jail, successfully pay the bill will allow ending the turn.
 *
 * @author dmytromuza
 */
@Getter

@EqualsAndHashCode(callSuper = true)
public final class JailFine extends Tax {

    JailFine(int value) {
        super(ActionType.OBLIGATION, DEFAULT_PRIORITY, value);
    }

    public static ActionCard create(int value) {
        return new JailFine(value);
    }

    @Override
    protected void check(Turn turn) throws TurnException {
        if (turn.getPlayerStatus() != PlayerStatus.IN_JAIL) {
            throw new TurnException(TurnError.PLAYER_IS_NOT_IN_JAIL_CANNOT_PAY_TAX);
        }
    }

    @Override
    protected List<ActionCard> onSuccess(Turn turn) {
        try {
            turn.leaveJail();
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error while leaving jail on " + turn, e);
        }
        return ImmutableList.of();
    }
}
