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
 * This is a chance card that allows a player to move on a specific land.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class GetOrPay extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(GetOrPay.class);

    private final int landId;

    GetOrPay(int landId) {
        super("Get or Pay", Action.MOVE, Type.CHOOSE, DEFAULT_PRIORITY);
        this.landId = landId;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return ImmutableList.of(new MoveTo(landId));
    }
}
