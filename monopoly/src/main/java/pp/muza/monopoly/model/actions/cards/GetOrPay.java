package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;

import java.util.List;

/**
 * This a chance card that allows the player to move on a specific land.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class GetOrPay extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(GetOrPay.class);

    private final int landId;

    GetOrPay(int landId) {
        super("Get or pay", Action.MOVE, Type.CHANCE, DEFAULT_PRIORITY);
        this.landId = landId;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return ImmutableList.of(new MoveTo(landId));
    }
}
