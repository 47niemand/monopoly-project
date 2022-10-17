package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.TurnError;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Offer;
import pp.muza.monopoly.model.Turn;

/**
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PromoteAuction extends BaseActionCard implements Offer, SyncCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    private final int position;
    @EqualsAndHashCode.Exclude
    private final int price;

    PromoteAuction(int position, int price) {
        super(Action.OFFER, ActionType.CHOOSE, DEFAULT_PRIORITY);
        this.position = position;
        this.price = price;
    }

    public static Offer of(int position, int price) {
        return new PromoteAuction(position, price);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        // initiate auction
        try {
            if (price > 0) {
                turn.sendOfferToAll(this);
            } else {
                throw new TurnException(TurnError.PLAYER_MUST_SET_PRICE_FOR_AUCTION);
            }
        } catch (TurnException e) {
            // consider this as a pass
            LOG.warn("PromoteAuction failed: {}", e.getMessage());
        }
        return ImmutableList.of();
    }

    @Override
    public Offer openingBid(int startPrice) {
        return new PromoteAuction(this.position, startPrice);
    }

    @Override
    public BaseActionCard sync(SyncCard card) {
        BaseActionCard result;
        if (this.equals(card)) {
            Offer other = (Offer) card;
            result = new PromoteAuction(this.position, Math.max(this.price, other.getPrice()));
        } else {
            throw new IllegalArgumentException("Cannot sync " + this + " with " + card);
        }
        return result;
    }
}

