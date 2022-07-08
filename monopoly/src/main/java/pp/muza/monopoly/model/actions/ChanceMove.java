package pp.muza.monopoly.model.actions;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.game.Turn;

import java.util.List;

/**
 * The player can decide to move on a specific distance.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ChanceMove extends AbstractActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(ChanceMove.class);

    private final int distance;

    ChanceMove(int distance) {
        super("Chance to move", Action.MOVE, Type.CHANCE, DEFAULT_PRIORITY);
        assert distance > 0;
        this.distance = distance;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.info("{} moving by {} steps", turn.getPlayer(), distance);
        int position = turn.nextPosition(distance);
        return ImmutableList.of(new MoveTo(position));
    }
}
