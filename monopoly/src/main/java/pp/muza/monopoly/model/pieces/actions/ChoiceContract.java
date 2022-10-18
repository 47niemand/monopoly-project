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
 * This card spawns action cards for the player, when player arrives at a land.
 * <p>For example:
 * <ul>
 * <li>when player arrives at the property, depending on the property owner, player can buy it, or player should pay rent.</li>
 * <li>when player arrives at the goto jail, player should move to jail.</li>
 * <li>when player arrives at the chance, player should draw a card.</li>
 * <li>etc.</li>
 * </ul>
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
