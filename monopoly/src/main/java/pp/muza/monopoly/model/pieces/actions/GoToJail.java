package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * This card lets a player go to jail.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class GoToJail extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(GoToJail.class);

    GoToJail() {
        super(Action.GO_TO_JAIL, ActionType.OBLIGATION, DEFAULT_PRIORITY);
    }

    public static ActionCard create() {
        return new GoToJail();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.setPlayerInJail();
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error during executing the action: " + this, e);
        }
        return ImmutableList.of(new EndTurn());
    }
}
