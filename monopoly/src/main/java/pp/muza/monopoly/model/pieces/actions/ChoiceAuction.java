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
 * A Player can choose this action to start an auction
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ChoiceAuction extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(ChoiceAuction.class);

    ChoiceAuction() {
        super(Action.DEFAULT, ActionType.CHOICE, HIGHEST_PRIORITY);
    }

    public static ActionCard create() {
        return new ChoiceAuction();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return CardUtils.createAuction(turn);
    }

}
