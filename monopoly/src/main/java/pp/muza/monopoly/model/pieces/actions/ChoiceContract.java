package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * A Player can choose this action to create contract for player's position
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ChoiceContract extends BaseActionCard {

    ChoiceContract() {
        super(Action.DEFAULT, ActionType.CHOICE, HIGHEST_PRIORITY);
    }

    public static ActionCard create() {
        return new ChoiceContract();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return CardUtils.createContract(turn);
    }
}
