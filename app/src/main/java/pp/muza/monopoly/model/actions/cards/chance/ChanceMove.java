package pp.muza.monopoly.model.actions.cards.chance;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.cards.ActionUtils;
import pp.muza.monopoly.model.turn.Turn;

/**
 * The player can decide to move on a specific distance.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ChanceMove extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(ChanceMove.class);

    private final int distance;

    ChanceMove(int distance) {
        super("Chance to move", Action.MOVE, Type.CHANCE, DEFAULT_PRIORITY);
        assert distance > 0;
        this.distance = distance;
    }

    @Override
    protected void onExecute(Turn turn) {
       ActionUtils.onMove(turn, distance);
    }
}
