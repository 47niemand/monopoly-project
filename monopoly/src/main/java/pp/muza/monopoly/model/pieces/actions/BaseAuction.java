package pp.muza.monopoly.model.pieces.actions;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.*;

import java.util.List;

/**
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAuction extends BaseActionCard implements Offer {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    protected final int position;
    /**
     * The starting price of the auction.
     */
    @EqualsAndHashCode.Exclude
    protected final int price;

    protected BaseAuction(ActionType actionType, int priority, int position, int price) {
        super(Action.OFFER, actionType, priority);
        this.position = position;
        this.price = price;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return ImmutableList.of();
    }

    @Override
    public Offer bid(int price) {
        throw new UnsupportedOperationException("Opening bid is not supported for the action: " + this);
    }
}

