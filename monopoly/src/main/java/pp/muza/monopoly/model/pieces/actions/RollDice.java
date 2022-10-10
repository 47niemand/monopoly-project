package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * A player must roll dice by using this card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RollDice extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(RollDice.class);

    RollDice() {
        super(Action.ROLL_DICE, ActionType.OBLIGATION, DEFAULT_PRIORITY);
    }

    public static ActionCard of() {
        return new RollDice();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        int dice = (int) (Math.random() * 6) + 1;
        LOG.info("{} rolled {}", turn.getPlayer().getName(), dice);
        return ImmutableList.of(new Move(dice));
    }
}
