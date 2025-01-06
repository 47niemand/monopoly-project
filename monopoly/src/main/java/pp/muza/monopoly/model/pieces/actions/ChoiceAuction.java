package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

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
