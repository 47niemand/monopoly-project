package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Biding;
import pp.muza.monopoly.model.Turn;

/**
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class EndAuction extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    EndAuction() {
        super(Action.AUCTION, ActionType.OBLIGATION, HIGHEST_PRIORITY);
    }

    public static ActionCard of(int position, int price) {
        return new EndAuction();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result = ImmutableList.of();
        try {
            Biding biding = turn.endAuction();
            if (biding != null) {
                result = ImmutableList.of(Sale.of(biding.getPosition(), biding.getPrice(), biding.getBidder()));
                LOG.info("Auction is ended. The winner is {}", biding.getBidder().getName());
            } else {
                LOG.info("No biding was made");
            }
        } catch (TurnException e) {
            LOG.error("Error during executing the action: {}", this, e);
            throw new UnexpectedErrorException(e);
        }
        return result;
    }
}

