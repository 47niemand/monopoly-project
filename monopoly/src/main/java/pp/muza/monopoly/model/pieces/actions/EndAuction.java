package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Biding;
import pp.muza.monopoly.model.Offer;
import pp.muza.monopoly.model.Turn;

/**
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class EndAuction extends BaseAuction implements Offer {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    EndAuction(int position, int price) {
        super(ActionType.OBLIGATION, HIGH_PRIORITY, position, price);
    }

    public static ActionCard create(int position, int price) {
        return new EndAuction(position, price);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result = ImmutableList.of();
        try {
            Biding biding = turn.endAuction(position);
            if (biding != null) {
                result = ImmutableList.of(new Sale(biding.getPosition(), biding.getPrice(), biding.getBidder()));
                LOG.info("Auction is ended. The winner is {} with price {}", biding.getBidder(), biding.getPrice());
            } else {
                LOG.info("No biding was made");
            }
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error during executing the action: " + this, e);
        }
        return result;
    }

    @Override
    public Offer bid(int price) {
        return this;
    }
}

