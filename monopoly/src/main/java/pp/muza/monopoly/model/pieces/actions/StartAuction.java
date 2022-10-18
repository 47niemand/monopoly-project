package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * By playing this card, the player starts the auction.
 *
 * @author dmytromuza
 */
@Getter

@EqualsAndHashCode(callSuper = true)
public final class StartAuction extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    StartAuction() {
        super(Action.AUCTION, ActionType.CHOOSE, DEFAULT_PRIORITY);
    }

    public static StartAuction create() {
        return new StartAuction();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return CardUtils.createAuction(turn);
    }
}

