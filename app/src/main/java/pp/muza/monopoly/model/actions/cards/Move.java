package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.turn.Turn;

/**
 * The player specifies the distance to take to move to a new location on the board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Move extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Move.class);

    private final int distance;

    Move(int distance) {
        super("Move", Action.MOVE, Type.OBLIGATION, DEFAULT_PRIORITY);
        assert distance > 0;
        this.distance = distance;
    }

    public static ActionCard of(int distance) {
        return new Move(distance);
    }

    @Override
    protected void onExecute(Turn turn) {
        ActionUtils.onMove(turn, distance);
    }
}
