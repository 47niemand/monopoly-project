package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.turn.Turn;

/**
 * This card lets the player go to jail.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class GoToJail extends ActionCard {
    GoToJail() {
        super("Go to Jail", Action.GO_TO_JAIL, Type.OBLIGATION, DEFAULT_PRIORITY);
    }

    @Override
    protected void onExecute(Turn turn) {
        turn.setPlayerInJail();
    }
}
