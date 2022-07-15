package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;

/**
 * A player can decide to move on a specific distance.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class OptionMove extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(OptionMove.class);

    private final int distance;

    OptionMove(int distance) {
        super("FortuneCard to move", Action.MOVE, Type.CHOOSE, DEFAULT_PRIORITY);
        assert distance > 0;
        this.distance = distance;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.info("{} chose to move by {} steps", turn.getPlayer().getName(), distance);
        int position = turn.nextPosition(distance);
        return ImmutableList.of(new MoveTo(position));
    }
}
