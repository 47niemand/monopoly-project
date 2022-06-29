package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.turn.Turn;

/**
 * The player moves to a new position on the board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MoveTo extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(MoveTo.class);

    private final int landId;

    MoveTo(int landId) {
        super("MoveTo", Action.MOVE, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.landId = landId;
    }

    public static ActionCard of(int landId) {
        return new MoveTo(landId);
    }

    @Override
    protected void onExecute(Turn turn) {
        ActionUtils.onMoveTo(turn, landId);
    }
}
