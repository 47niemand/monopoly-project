package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Offer;
import pp.muza.monopoly.model.Turn;

/**
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAuction extends BaseActionCard implements Offer {

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
