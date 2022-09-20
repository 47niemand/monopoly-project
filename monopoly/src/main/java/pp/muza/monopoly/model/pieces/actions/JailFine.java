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
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Turn;

/**
 * If the player is in jail, successfully pay the bill will allow ending the turn.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class JailFine extends Tax {

    private static final Logger LOG = LoggerFactory.getLogger(JailFine.class);

    JailFine(int number) {
        super("Jail Fine", ActionType.OBLIGATION, DEFAULT_PRIORITY, number);
    }

    @Override
    protected void check(Turn turn) throws TurnException {
        if (turn.getStatus() != PlayerStatus.IN_JAIL) {
            throw new TurnException("Player is not in jail, cannot pay tax");
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
