package pp.muza.monopoly.model.actions;

import java.util.List;
import java.util.stream.Collectors;

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
        LOG.info("Executing card {}", actionCard);
        List<ActionCard> result = ((AbstractActionCard) actionCard).onExecute(turn);
        LOG.info("Resulting: {}", result.stream().map(ActionCard::getName).collect(Collectors.toList()));
        return result;
    }

}
