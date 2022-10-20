package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.GameError;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Offer;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class PromoteAuction extends BaseActionCard implements Offer, SyncCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    private final int position;
    @EqualsAndHashCode.Exclude
    private final int price;

    PromoteAuction(int position, int price) {
        super(Action.OFFER, ActionType.CHOOSE, HIGH_PRIORITY);
        this.position = position;
        this.price = price;
    }

    public static Offer create(int position, int price) {
        return new PromoteAuction(position, price);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        // initiate auction
        try {
            if (price > 0) {
                LOG.info("Promote an auction for property {} with price {}", position, price);
                turn.auction(position, price);
                Player seller = turn.getPlayer();
                for (Player bidder : turn.getPlayers()) {
                    // Sending an auction invitation to all players.
                    if (bidder != seller && !turn.getPlayerStatus(bidder).isFinal()) {
                        try {
                            turn.sendCard(bidder, new Bid(position, price));
                            turn.sendCard(bidder, new EndTurn());
                        } catch (TurnException e) {
                            throw new UnexpectedErrorException("Error while sending invitation to player {}" + bidder, e);
                        }
                    }
                }
                turn.holdTurn();
            } else {
                throw new TurnException(GameError.PLAYER_MUST_SET_PRICE_FOR_AUCTION);
            }
            return ImmutableList.of(new EndAuction(position, price));
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
            result = new PromoteAuction(this.position, other.getPrice());
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

