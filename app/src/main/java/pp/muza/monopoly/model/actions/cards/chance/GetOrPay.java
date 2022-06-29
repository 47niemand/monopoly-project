package pp.muza.monopoly.model.actions.cards.chance;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.cards.ActionUtils;
import pp.muza.monopoly.model.turn.Turn;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class GetOrPay extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(GetOrPay.class);

    private final int landId;

    GetOrPay(int landId) {
        super("GetOrPay", Action.MOVE, Type.CHANCE, DEFAULT_PRIORITY);
        this.landId = landId;
    }

    @Override
    protected void onExecute(Turn turn) {
        ActionUtils.onMoveTo(turn, landId);
    }
}
