package pp.muza.monopoly.model.actions.strategy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;


/**
 * This card finishes the turn.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class EndTurn extends ActionCard {


    EndTurn() {
        super("End Turn", ActionType.END_TURN, 10000, true);
    }

    @Override
    protected void onExecute(Turn turn) {
        turn.finish();
        turn.getGame().endTurn(turn);
    }


}
