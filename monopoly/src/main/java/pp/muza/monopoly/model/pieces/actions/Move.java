package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Turn;

/**
 * A player specifies the distance to take to move to a new location on the
 * board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Move extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Move.class);

    protected final int distance;

    protected Move(String name, ActionType type, int priority, int distance) {
        super(name, Action.MOVE, type, priority);
        this.distance = distance;
    }

    Move(int distance) {
        this("Move", ActionType.OBLIGATION, DEFAULT_PRIORITY, distance);
    }

    public static ActionCard of(int distance) {
        return new Move(distance);
    }

    /**
     * A method called when the player arrives at a new location on the board.
     * <p>can be overridden by subclasses to perform additional actions.</p>
     *
     * @param turn     the current turn
     * @param position the new location on the board.
     * @return the action cards to execute after the player arrives at the new location.
     */
    @SuppressWarnings("unused")
    protected List<ActionCard> onArrival(Turn turn, int position) {
        return ImmutableList.of(new Arrival(position));
    }

    @Override
    protected final List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        if (distance <= 0) {
            LOG.warn("Distance must be positive");
            result = ImmutableList.of();
        } else {
            int position = turn.nextPosition(distance);
            LOG.info("{}: advancing by {} steps to {} ({})", turn.getPlayer().getName(), distance, position,
                    turn.getLand(position).getName());
            List<Land> path;
            try {
                path = turn.moveTo(position);
            } catch (TurnException e) {
                LOG.error("Error during executing the action: {}", this, e);
                throw new RuntimeException(e);
            }
            assert path.size() == distance;
            result = ImmutableList.<ActionCard>builder()
                    .addAll(CardUtils.onPath(turn, path))
                    .addAll(onArrival(turn, position))
                    .build();
        }
        return result;
    }
}
