package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

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

    StartAuction() {
        super(Action.AUCTION, ActionType.OPTIONAL, DEFAULT_PRIORITY);
    }

    public static ActionCard create() {
        return new StartAuction();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return CardUtils.createAuction(turn);
    }
}
