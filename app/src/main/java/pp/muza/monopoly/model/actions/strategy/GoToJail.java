package pp.muza.monopoly.model.actions.strategy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.game.Turn;

/**
 * This card lets the player go to jail.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class GoToJail extends ActionCard {
    GoToJail() {
        super("Go to Jail", ActionType.GO_TO_JAIL, 10, true);
    }

    @Override
    protected void onExecute(Turn turn) {
        turn.getGame().setPlayerStatus(turn.getPlayer(), Game.PlayerStatus.IN_JAIL);
        turn.getGame().setPlayerPos(turn.getPlayer(), turn.getGame().getJailPos());
    }


}
