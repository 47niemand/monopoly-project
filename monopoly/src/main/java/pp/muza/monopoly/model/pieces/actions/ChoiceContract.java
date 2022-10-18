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
 * A Player can choose this action to create contract for player's position
 *
 * @author dmytromuza
 */
@Getter

@EqualsAndHashCode(callSuper = true)
public class ChoiceContract extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(ChoiceContract.class);

    ChoiceContract() {
        super(Action.CHOICE, ActionType.CHOOSE, HIGHEST_PRIORITY);
    }

    public static ActionCard create() {
        return new ChoiceContract();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return CardUtils.createContract(turn);
    }
}
