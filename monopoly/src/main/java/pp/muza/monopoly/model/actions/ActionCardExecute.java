package pp.muza.monopoly.model.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.game.Turn;

public class ActionCardExecute {

    private static final Logger LOG = LoggerFactory.getLogger(ActionCardExecute.class);

    /**
     * it is only one way to execute action card.
     *
     * @param turn       the turn of the game.
     * @param actionCard the action card to execute.
     * @return list of action cards spawned by the action card.
     */
    public static List<ActionCard> execute(Turn turn, ActionCard actionCard) {
        LOG.debug("Executing card {} for player {}", actionCard, turn.getPlayer().getName());
        List<ActionCard> result = ((BaseActionCard) actionCard).onExecute(turn);
        LOG.debug("Resulting: {}", result);
        return result;
    }

}
