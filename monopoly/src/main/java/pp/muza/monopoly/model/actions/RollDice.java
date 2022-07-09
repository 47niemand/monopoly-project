package pp.muza.monopoly.model.actions;

import java.util.List;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.game.Turn;

/**
 * The player must roll dice by using this card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RollDice extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(RollDice.class);

    RollDice() {
        super("Roll Dice", Action.ROLL_DICE, Type.OBLIGATION, NEW_TURN_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        int dice = (int) (Math.random() * 6) + 1;
        LOG.info("Player {} rolled {}", turn.getPlayer().getName(), dice);
        return ImmutableList.of(new Move(dice));
    }
}
