package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.turn.Turn;


/**
 * This card finishes the turn.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class EndTurn extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(EndTurn.class);

    EndTurn() {
        super("End Turn", Action.END_TURN, Type.OBLIGATION, LOW_PRIORITY);
    }

    @Override
    protected void onExecute(Turn turn) {
        LOG.info("End turn");
        turn.endTurn();
    }
}
