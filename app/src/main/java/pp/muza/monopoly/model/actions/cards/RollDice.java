package pp.muza.monopoly.model.actions.cards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.turn.Turn;

/**
 * The player must roll dice by using this card.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class RollDice extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(RollDice.class);

    RollDice() {
        super("Roll Dice", Action.ROLL_DICE, Type.OBLIGATION, DEFAULT_PRIORITY);
    }

    @Override
    protected void onExecute(Turn turn) {
        int dice = turn.rollDice();
        LOG.info("Player rolled {}", dice);
        turn.addActionCard(new Move(dice));
    }
}
