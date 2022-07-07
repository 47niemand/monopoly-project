package pp.muza.monopoly.model.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.game.Turn;

import java.util.List;

public class ActionCardExecute {

    private static final Logger LOG = LoggerFactory.getLogger(ActionCardExecute.class);

    public static List<ActionCard> execute(Turn turn, ActionCard actionCard) {
        LOG.info("Executing card {}", actionCard);
        List<ActionCard> result = actionCard.onExecute(turn);
        LOG.info("Resulting: {}", result);
        return result;
    }

}
