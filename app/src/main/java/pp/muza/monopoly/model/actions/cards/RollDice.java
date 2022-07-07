package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;

import java.util.List;

/**
 * The player must roll dice by using this card.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class RollDice extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(RollDice.class);

    RollDice() {
        super("Roll Dice", Action.ROLL_DICE, Type.OBLIGATION, NEW_TURN_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        int dice = (int) (Math.random() * 6) + 1;
        LOG.info("Player rolled {}", dice);
        return ImmutableList.of(new Move(dice));
    }
}
