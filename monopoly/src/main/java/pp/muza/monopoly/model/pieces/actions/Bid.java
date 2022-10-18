package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.BidingAction;
import pp.muza.monopoly.model.Turn;

/**
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class Bid extends BaseActionCard implements BidingAction, SyncCard {

    private static final Logger LOG = LoggerFactory.getLogger(Bid.class);

    private final int position;
    @EqualsAndHashCode.Exclude
    private final int price;

    Bid(int position, int price) {
        super(Action.BID, ActionType.CHOOSE, DEFAULT_PRIORITY);
        this.position = position;
        this.price = price;
    }

    public static BidingAction create(int position, int price) {
        return new Bid(position, price);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.doBid(position, price);
        } catch (TurnException e) {
            LOG.warn("Bid failed: {}", e.getMessage());
            return ImmutableList.of(this);
        }
        return ImmutableList.of();
    }

    @Override
    public BidingAction bid(int newPrice) {
        return new Bid(this.position, newPrice);
    }

    @Override
    public BaseActionCard sync(SyncCard card) {
        BaseActionCard result;
        if (this.equals(card)) {
            BidingAction other = (BidingAction) card;
            result = new Bid(this.position, other.getPrice());
        } else {
            throw new IllegalArgumentException("Cannot sync " + this + " with " + card);
        }
        return result;
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                Map.of("position", position, "price", price)
        );
    }
}