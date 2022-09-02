package pp.muza.monopoly.model.pieces.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A player can decide to move on a specific distance.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class OptionMove extends Move {

    private static final Logger LOG = LoggerFactory.getLogger(OptionMove.class);

    OptionMove(int distance) {
        super("Choice to Move", ActionType.CHOOSE, DEFAULT_PRIORITY, distance);
    }
}
