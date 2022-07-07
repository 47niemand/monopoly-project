package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;

import java.util.List;

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
    protected List<ActionCard> onExecute(Turn turn) {
        int position = turn.nextPosition(distance);
        LOG.info("{} moving by {} steps to {} ({})", turn.getPlayer(), distance, position, turn.getLand(position).getName());
        List<ActionCard> res;
        List<Land> path = turn.moveTo(position);
        assert path.size() == distance;
        res = MoveTo.onArrival(turn, path, position);
        return res;

    }
}
