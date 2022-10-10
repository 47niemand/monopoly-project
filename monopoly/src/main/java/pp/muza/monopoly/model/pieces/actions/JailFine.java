package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.TurnError;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Turn;

/**
 * If the player is in jail, successfully pay the bill will allow ending the turn.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class JailFine extends Tax {

    JailFine(int value) {
        super(ActionType.OBLIGATION, DEFAULT_PRIORITY, value);
    }

    public static ActionCard of(int value) {
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
            throw new IllegalStateException(e);
        }
        return ImmutableList.of();
    }
}
