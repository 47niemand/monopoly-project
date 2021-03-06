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
 * A player specifies the distance to take to move to a new location on the
 * board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Move extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Move.class);

    private final int distance;

    Move(int distance) {
        super("Move", Action.MOVE, Type.OBLIGATION, DEFAULT_PRIORITY);
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }
        this.distance = distance;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        int position = turn.nextPosition(distance);
        LOG.info("{}: moving by {} steps to {} ({})", turn.getPlayer().getName(), distance, position,
                turn.getLand(position).getName());
        List<Land> path = turn.moveTo(position);
        assert path.size() == distance;
        return ImmutableList.<ActionCard>builder()
                .addAll(CardUtils.onPath(turn, path))
                .add(new Arrival(position))
                .build();
    }
}
