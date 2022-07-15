package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Turn;

/**
 * A player moves to a new position on the board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MoveTo extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(MoveTo.class);

    private final int position;

    MoveTo(int position) {
        super("MoveTo", Action.MOVE, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.position = position;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> res;
        LOG.info("{} moved to {} ({})", turn.getPlayer().getName(), position, turn.getLand(position).getName());
        List<Land> path = turn.moveTo(position);
        if (path.size() == 0) {
            LOG.warn("Staying on the same land");
            res = ImmutableList.of();
        } else {
            res = CardUtils.onArrival(turn, path, position);
        }
        return res;
    }

}
