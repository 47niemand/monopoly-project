package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;

/**
 * This card lets a player go to jail.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class GoToJail extends BaseActionCard {
    GoToJail() {
        super("Go to Jail", Action.GO_TO_JAIL, Type.OBLIGATION, DEFAULT_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        turn.setPlayerInJail();
        return ImmutableList.of(new EndTurn());
    }
}
