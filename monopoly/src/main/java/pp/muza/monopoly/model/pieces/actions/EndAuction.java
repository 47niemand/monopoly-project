package pp.muza.monopoly.model.pieces.actions;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Biding;
import pp.muza.monopoly.model.Turn;

import java.util.List;

/**
 * @author dmytromuza
 */
@Getter

@EqualsAndHashCode(callSuper = true)
public final class EndAuction extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    EndAuction() {
        super(Action.AUCTION, ActionType.OBLIGATION, HIGHEST_PRIORITY);
    }

    public static ActionCard create() {
        return new EndAuction();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result = ImmutableList.of();
        try {
            Biding biding = turn.endAuction();
            if (biding != null) {
                result = ImmutableList.of(new Sale(biding.getPosition(), biding.getPrice(), biding.getBidder()));
                LOG.info("Auction is ended. The winner is {}", biding.getBidder());
            } else {
                LOG.info("No biding was made");
            }
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error during executing the action: " + this, e);
        }
        return result;
    }
}

