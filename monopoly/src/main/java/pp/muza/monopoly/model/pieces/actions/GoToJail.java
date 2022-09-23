package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * This card lets a player go to jail.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class GoToJail extends BaseActionCard {

    GoToJail() {
        super("Go to Jail", Action.GO_TO_JAIL, ActionType.OBLIGATION, DEFAULT_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.setPlayerInJail();
        } catch (pp.muza.monopoly.errors.TurnException e) {
            throw new RuntimeException(e);
        }
        return ImmutableList.of(new EndTurn());
    }
}
